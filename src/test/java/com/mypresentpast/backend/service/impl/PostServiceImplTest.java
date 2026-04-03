package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.CreatePostRequest;
import com.mypresentpast.backend.dto.request.UpdatePostRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.MapResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import com.mypresentpast.backend.enums.Category;
import com.mypresentpast.backend.enums.MediaType;
import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Location;
import com.mypresentpast.backend.model.Media;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.LocationRepository;
import com.mypresentpast.backend.repository.MediaRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.CloudinaryService;
import com.mypresentpast.backend.service.LikeService;
import com.mypresentpast.backend.service.PostVerificationQueryService;
import com.mypresentpast.backend.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private LikeService likeService;

    @Mock
    private PostVerificationQueryService verificationQueryService;

    @Mock
    private MultipartFile mockImage;

    @InjectMocks
    private PostServiceImpl postService;

    private User testUser;
    private User institutionUser;
    private Location testLocation;
    private Post testPost;
    private CreatePostRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setProfileUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("testuser@example.com");
        testUser.setRole(UserRole.NORMAL);

        institutionUser = new User();
        institutionUser.setId(2L);
        institutionUser.setProfileUsername("institution");
        institutionUser.setPassword("password");
        institutionUser.setEmail("institution@example.com");
        institutionUser.setRole(UserRole.INSTITUTION);

        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setAddress("Test Address");
        testLocation.setLatitude(-34.6118);
        testLocation.setLongitude(-58.3960);

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setDate(LocalDate.of(2024, 6, 15));
        testPost.setPostedAt(LocalDate.of(2024, 6, 15));
        testPost.setCategory(Category.STORY);
        testPost.setIsByIA(false);
        testPost.setStatus(PostStatus.ACTIVE);
        testPost.setAuthor(testUser);
        testPost.setLocation(testLocation);
        testPost.setMedia(new ArrayList<>());

        createRequest = new CreatePostRequest();
        createRequest.setTitle("New Test Post");
        createRequest.setContent("New test content");
        createRequest.setDate(LocalDate.of(2024, 6, 15));
        createRequest.setCategory(Category.STORY);
        createRequest.setIsByIA(false);
        createRequest.setAuthorId(1L);
        createRequest.setLatitude(-34.6118);
        createRequest.setLongitude(-58.3960);
        createRequest.setAddress("Buenos Aires, Argentina");
    }

    // ─── helpers privados ────────────────────────────────────────────────────

    private Map<String, Object> validUploadResultMock() {
        Map<String, Object> result = new HashMap<>();
        result.put("url", "http://res.cloudinary.com/test/image.jpg");
        result.put("public_id", "test_public_id_123");
        return result;
    }

    private void stubMapToPostResponse(Post post) {
        when(verificationQueryService.isPostVerified(post)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(post.getId())).thenReturn(null);
        when(likeService.getTotalLikes(post.getId())).thenReturn(0L);
        when(likeService.isLikedByCurrentUser(post.getId())).thenReturn(false);
    }

    // ─── createPost ──────────────────────────────────────────────────────────

    // Ubicación no existe en proximidad → se crea una nueva; no hay imágenes
    @Test
    void createPost_SinImagenesYUbicacionNueva_RetornaMensajeExito() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.emptyList());
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        ApiResponse response = postService.createPost(createRequest, null);

        assertNotNull(response);
        assertEquals("Publicación creada con éxito", response.getMessage());
        verify(userRepository).findById(1L);
        verify(locationRepository).findLocationsByProximity(-34.6118, -58.3960);
        verify(locationRepository).save(any(Location.class));
        verify(postRepository).save(any(Post.class));
        verify(cloudinaryService, never()).upload(any());
    }

    // Existe una ubicación cercana → se reutiliza sin crear una nueva
    @Test
    void createPost_SinImagenesYUbicacionExistente_ReutilizaLocation() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.singletonList(testLocation));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        ApiResponse response = postService.createPost(createRequest, null);

        assertNotNull(response);
        assertEquals("Publicación creada con éxito", response.getMessage());
        verify(locationRepository, never()).save(any(Location.class));
    }

    // Lista de imágenes vacía (distinto a null) → no se sube nada
    @Test
    void createPost_ListaImagenesVacia_NoSubeNadaACloudinary() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.emptyList());
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        ApiResponse response = postService.createPost(createRequest, Collections.emptyList());

        assertNotNull(response);
        assertEquals("Publicación creada con éxito", response.getMessage());
        verify(cloudinaryService, never()).upload(any());
        verify(mediaRepository, never()).saveAll(any());
    }

    // Una imagen válida → se sube a Cloudinary y se persiste el Media
    @Test
    void createPost_ConUnaImagen_SubeACloudinaryYGuardaMedia() throws Exception {
        Map<String, Object> uploadResult = validUploadResultMock();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.emptyList());
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(cloudinaryService.upload(mockImage)).thenReturn(uploadResult);
        when(mediaRepository.saveAll(any())).thenReturn(new ArrayList<>());

        ApiResponse response = postService.createPost(createRequest, Collections.singletonList(mockImage));

        assertNotNull(response);
        assertEquals("Publicación creada con éxito", response.getMessage());
        verify(cloudinaryService).upload(mockImage);

        ArgumentCaptor<List<Media>> mediaCaptor = ArgumentCaptor.forClass(List.class);
        verify(mediaRepository).saveAll(mediaCaptor.capture());
        List<Media> savedMedia = mediaCaptor.getValue();
        assertEquals(1, savedMedia.size());
        assertEquals("http://res.cloudinary.com/test/image.jpg", savedMedia.get(0).getUrl());
        assertEquals("test_public_id_123", savedMedia.get(0).getCloudinaryId());
        assertEquals(MediaType.IMAGE, savedMedia.get(0).getType());
    }

    // Seis imágenes superan el límite de 5 → debe lanzar excepción antes de subir
    @Test
    void createPost_MasDecincoImagenes_LanzaIllegalArgumentException() {
        List<MultipartFile> seisImagenes = Arrays.asList(
                mockImage, mockImage, mockImage, mockImage, mockImage, mockImage);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.emptyList());
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> postService.createPost(createRequest, seisImagenes));

        assertEquals("Máximo 5 imágenes permitidas por publicación", ex.getMessage());
        verify(cloudinaryService, never()).upload(any());
    }

    // Cloudinary lanza excepción al subir → se relanza como RuntimeException
    @Test
    void createPost_CloudinaryFalla_LanzaRuntimeException() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.emptyList());
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(cloudinaryService.upload(mockImage)).thenThrow(new RuntimeException("Timeout de red"));

        List<MultipartFile> images = Collections.singletonList(mockImage);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> postService.createPost(createRequest, images));

        assertTrue(ex.getMessage().contains("Error al subir imagen"));
    }

    // Usuario con id=1 no existe → excepción inmediata
    @Test
    void createPost_UsuarioNoExiste_LanzaResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.createPost(createRequest, null));

        assertEquals("Usuario no encontrado con id: 1", ex.getMessage());
    }

    // ─── getPostById ─────────────────────────────────────────────────────────

    // Post encontrado con autor y ubicación → se mapea correctamente
    @Test
    void getPostById_PostExistente_RetornaPostResponse() {
        when(postRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testPost));
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);
        when(likeService.getTotalLikes(1L)).thenReturn(5L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(true);

        PostResponse response = postService.getPostById(1L);

        assertNotNull(response);
        assertEquals("Test Post", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals("testuser", response.getAuthor().getName());
        assertEquals(5L, response.getTotalLikes());
        assertTrue(response.getIsLiked());
        verify(postRepository).findByIdWithRelations(1L);
        verify(likeService).getTotalLikes(1L);
        verify(likeService).isLikedByCurrentUser(1L);
        verify(verificationQueryService).isPostVerified(testPost);
        verify(verificationQueryService).getExternalVerifier(1L);
    }

    // Post verificado con institución externa → verifiedBy se popula en la respuesta
    @Test
    void getPostById_PostConVerificador_MapeoVerifiedByCompleto() {
        when(postRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testPost));
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(true);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(institutionUser);
        when(likeService.getTotalLikes(1L)).thenReturn(0L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(false);

        PostResponse response = postService.getPostById(1L);

        assertNotNull(response.getVerifiedBy());
        assertEquals(2L, response.getVerifiedBy().getId());
        assertEquals("institution", response.getVerifiedBy().getName());
        assertEquals(UserRole.INSTITUTION, response.getVerifiedBy().getType());
        assertTrue(response.getIsVerified());
    }

    // Post con dos imágenes → la lista de media se mapea en el response
    @Test
    void getPostById_PostConMedia_MapeoMediaCompleto() {
        Media media1 = new Media();
        media1.setId(10L);
        media1.setType(MediaType.IMAGE);
        media1.setUrl("http://example.com/img1.jpg");

        Media media2 = new Media();
        media2.setId(11L);
        media2.setType(MediaType.IMAGE);
        media2.setUrl("http://example.com/img2.jpg");

        testPost.setMedia(Arrays.asList(media1, media2));

        when(postRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testPost));
        stubMapToPostResponse(testPost);

        PostResponse response = postService.getPostById(1L);

        assertNotNull(response.getMedia());
        assertEquals(2, response.getMedia().size());
        assertEquals(10L, response.getMedia().get(0).getId());
        assertEquals("http://example.com/img1.jpg", response.getMedia().get(0).getUrl());
    }

    // Post sin location asignada → el campo location en la respuesta debe ser null
    @Test
    void getPostById_PostSinUbicacion_LocationEsNull() {
        testPost.setLocation(null);

        when(postRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testPost));
        stubMapToPostResponse(testPost);

        PostResponse response = postService.getPostById(1L);

        assertNull(response.getLocation());
    }

    // Post sin autor asignado → el campo author en la respuesta debe ser null
    @Test
    void getPostById_PostSinAutor_AutorEsNull() {
        testPost.setAuthor(null);

        when(postRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testPost));
        stubMapToPostResponse(testPost);

        PostResponse response = postService.getPostById(1L);

        assertNull(response.getAuthor());
    }

    // Post con id=1 no existe → excepción con mensaje exacto
    @Test
    void getPostById_PostNoEncontrado_LanzaResourceNotFoundException() {
        when(postRepository.findByIdWithRelations(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.getPostById(1L));

        assertEquals("Publicación no encontrada con id: 1", ex.getMessage());
        verify(postRepository).findByIdWithRelations(1L);
    }

    // ─── getPostsByUser ───────────────────────────────────────────────────────

    // Usuario con posts activos → se devuelven mapeados
    @Test
    void getPostsByUser_UsuarioConPosts_RetornaListaDeResponses() {
        when(postRepository.findByAuthorIdAndStatus(1L, PostStatus.ACTIVE))
                .thenReturn(Collections.singletonList(testPost));
        stubMapToPostResponse(testPost);

        List<PostResponse> responses = postService.getPostsByUser(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Post", responses.get(0).getTitle());
        verify(postRepository).findByAuthorIdAndStatus(1L, PostStatus.ACTIVE);
    }

    // Usuario sin posts activos → excepción con mensaje estándar
    @Test
    void getPostsByUser_UsuarioSinPosts_LanzaResourceNotFoundException() {
        when(postRepository.findByAuthorIdAndStatus(1L, PostStatus.ACTIVE))
                .thenReturn(Collections.emptyList());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.getPostsByUser(1L));

        assertEquals("No hay publicaciones disponibles para mostrar", ex.getMessage());
    }

    // ─── getLikedPostsByCurrentUser ───────────────────────────────────────────

    // Usuario autenticado tiene posts likeados → se mapean y devuelven
    @Test
    void getLikedPostsByCurrentUser_UsuarioConLikes_RetornaListaDeResponses() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(postRepository.findLikedPostsByUserId(1L))
                    .thenReturn(Collections.singletonList(testPost));
            when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
            when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);
            when(likeService.getTotalLikes(1L)).thenReturn(10L);
            when(likeService.isLikedByCurrentUser(1L)).thenReturn(true);

            List<PostResponse> response = postService.getLikedPostsByCurrentUser();

            assertNotNull(response);
            assertEquals(1, response.size());
            assertEquals("Test Post", response.get(0).getTitle());
            assertEquals(10L, response.get(0).getTotalLikes());
            assertTrue(response.get(0).getIsLiked());
            verify(postRepository).findLikedPostsByUserId(1L);
        }
    }

    // Usuario autenticado no tiene ningún post likeado → lista vacía sin excepción
    @Test
    void getLikedPostsByCurrentUser_UsuarioSinLikes_RetornaListaVacia() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(postRepository.findLikedPostsByUserId(1L)).thenReturn(new ArrayList<>());

            List<PostResponse> response = postService.getLikedPostsByCurrentUser();

            assertNotNull(response);
            assertTrue(response.isEmpty());
            verify(postRepository).findLikedPostsByUserId(1L);
        }
    }

    // ─── getRandomPost ────────────────────────────────────────────────────────

    // Hay al menos un post activo con ubicación → se devuelve correctamente
    @Test
    void getRandomPost_HayPostsActivos_RetornaUnPost() {
        when(postRepository.findByStatusAndLocationIsNotNull(PostStatus.ACTIVE))
                .thenReturn(Collections.singletonList(testPost));
        stubMapToPostResponse(testPost);

        PostResponse response = postService.getRandomPost();

        assertNotNull(response);
        assertEquals("Test Post", response.getTitle());
        verify(postRepository).findByStatusAndLocationIsNotNull(PostStatus.ACTIVE);
    }

    // No existe ningún post activo con ubicación → excepción con mensaje estándar
    @Test
    void getRandomPost_NoHayPostsActivos_LanzaResourceNotFoundException() {
        when(postRepository.findByStatusAndLocationIsNotNull(PostStatus.ACTIVE))
                .thenReturn(Collections.emptyList());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.getRandomPost());

        assertEquals("No hay publicaciones disponibles para mostrar", ex.getMessage());
    }

    // ─── getMapData ───────────────────────────────────────────────────────────

    // Sin filtros opcionales → se delega la query con listas vacías y nulls
    @Test
    void getMapData_SinFiltros_RetornaTodosLosPostsDelArea() {
        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), null, null, null, Collections.emptyList()))
                .thenReturn(Collections.singletonList(testPost));
        stubMapToPostResponse(testPost);

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                null, null, null, null, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        assertEquals("Test Post", response.getPosts().get(0).getTitle());
    }

    // Se envía una sola categoría via parámetro 'category' → se convierte a enum y se pasa a la query
    @Test
    void getMapData_ConCategoriaSimple_MapeoACategoryEnum() {
        List<String> expectedCategories = Collections.singletonList("STORY");

        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                expectedCategories, null, null, null, Collections.emptyList()))
                .thenReturn(Collections.singletonList(testPost));
        stubMapToPostResponse(testPost);

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                "STORY", null, null, null, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
    }

    // Se envían tanto 'category' como 'categories' → 'categories' tiene prioridad
    @Test
    void getMapData_ConCategoriasMultiples_PriorizaCategoriesOverCategory() {
        List<String> expectedCategories = Arrays.asList("STORY", "INFORMATION");

        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                expectedCategories, null, null, null, Collections.emptyList()))
                .thenReturn(Collections.singletonList(testPost));
        stubMapToPostResponse(testPost);

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                "MYTH", Arrays.asList("STORY", "INFORMATION"),
                null, null, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
    }

    // Categoría que no existe en el enum → se ignora con warning y la lista queda vacía
    @Test
    void getMapData_CategoriaInvalida_SeIgnoraYListaQuedaVacia() {
        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), null, null, null, Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                "CATEGORIA_INEXISTENTE", null,
                null, null, null, null, null, null, null);

        assertNotNull(response);
        assertTrue(response.getPosts().isEmpty());
    }

    // Solo se envía 'date' sin dateFrom/dateTo → dateFrom y dateTo toman el mismo valor
    @Test
    void getMapData_ConFechaSimple_CreoRangoConMismaFechaParaAmbosBound() {
        LocalDate fecha = LocalDate.of(2024, 3, 15);

        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), fecha, fecha, null, Collections.emptyList()))
                .thenReturn(Collections.singletonList(testPost));
        stubMapToPostResponse(testPost);

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                null, null, fecha, null, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        verify(postRepository).findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), fecha, fecha, null, Collections.emptyList());
    }

    // Se pasan dateFrom y dateTo explícitos → se usan sin modificar
    @Test
    void getMapData_ConDateFromYDateTo_UsaRangoExplicito() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), from, to, null, Collections.emptyList()))
                .thenReturn(Collections.singletonList(testPost));
        stubMapToPostResponse(testPost);

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                null, null, null, from, to, null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
    }

    // Se envía userId individual pero no userIds → se agrega ese id a la lista
    @Test
    void getMapData_ConUserIdSinUserIds_AgregaUserIdALaLista() {
        List<Long> expectedUserIds = Collections.singletonList(1L);

        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), null, null, null, expectedUserIds))
                .thenReturn(Collections.singletonList(testPost));
        stubMapToPostResponse(testPost);

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                null, null, null, null, null, null, null, 1L, null);

        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
    }

    // Se envían tanto userId como userIds → userIds tiene prioridad
    @Test
    void getMapData_ConUserIds_PriorizaUserIdsOverUserId() {
        List<Long> expectedUserIds = Arrays.asList(1L, 2L);

        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), null, null, null, expectedUserIds))
                .thenReturn(Collections.singletonList(testPost));
        stubMapToPostResponse(testPost);

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                null, null, null, null, null, null, null, 99L, Arrays.asList(1L, 2L));

        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
    }

    // isVerified=true → solo pasan posts cuyo estado de verificación es true
    @Test
    void getMapData_FiltroIsVerifiedTrue_ExcluyePostsNoVerificados() {
        Post postNoVerificado = new Post();
        postNoVerificado.setId(2L);
        postNoVerificado.setTitle("Post No Verificado");
        postNoVerificado.setAuthor(testUser);
        postNoVerificado.setLocation(testLocation);
        postNoVerificado.setMedia(new ArrayList<>());

        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), null, null, null, Collections.emptyList()))
                .thenReturn(Arrays.asList(testPost, postNoVerificado));

        when(verificationQueryService.isPostVerified(testPost)).thenReturn(true);
        when(verificationQueryService.isPostVerified(postNoVerificado)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);
        when(likeService.getTotalLikes(1L)).thenReturn(0L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(false);

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                null, null, null, null, null, true, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        assertEquals("Test Post", response.getPosts().get(0).getTitle());
        assertTrue(response.getPosts().get(0).getIsVerified());
    }

    // isVerified=null → no se aplica filtro; todos los posts del área se incluyen
    @Test
    void getMapData_FiltroIsVerifiedNull_NoFiltroDeVerificacion() {
        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), null, null, null, Collections.emptyList()))
                .thenReturn(Collections.singletonList(testPost));
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);
        when(likeService.getTotalLikes(1L)).thenReturn(0L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(false);

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                null, null, null, null, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
    }

    // Query principal devuelve vacío con rango de fechas → se ejecuta la query de debug sobre findAll
    @Test
    void getMapData_ResultadoVacioConFiltroFecha_EjecutaDebugQuery() {
        LocalDate from = LocalDate.of(2024, 6, 15);
        LocalDate to = LocalDate.of(2024, 6, 15);

        Post postActivo = new Post();
        postActivo.setId(5L);
        postActivo.setStatus(PostStatus.ACTIVE);
        postActivo.setDate(LocalDate.of(2024, 6, 15));
        postActivo.setLocation(testLocation);

        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), from, to, null, Collections.emptyList()))
                .thenReturn(Collections.emptyList());
        when(postRepository.findAll()).thenReturn(Collections.singletonList(postActivo));

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                null, null, null, from, to, null, null, null, null);

        assertNotNull(response);
        assertTrue(response.getPosts().isEmpty());
        verify(postRepository).findAll();
    }

    // Post en rango de fecha pero sin ubicación → rama debug que loguea "SIN UBICACIÓN"
    @Test
    void getMapData_ResultadoVacioConFiltroFechaYPostSinLocation_RamaDebugSinLocation() {
        LocalDate from = LocalDate.of(2024, 6, 15);
        LocalDate to = LocalDate.of(2024, 6, 15);

        Post postSinLocation = new Post();
        postSinLocation.setId(6L);
        postSinLocation.setStatus(PostStatus.ACTIVE);
        postSinLocation.setDate(LocalDate.of(2024, 6, 15));
        postSinLocation.setLocation(null);

        when(postRepository.findPostsInAreaWithMultipleFilters(
                -35.0, -34.0, -59.0, -58.0,
                Collections.emptyList(), from, to, null, Collections.emptyList()))
                .thenReturn(Collections.emptyList());
        when(postRepository.findAll()).thenReturn(Collections.singletonList(postSinLocation));

        MapResponse response = postService.getMapData(
                -35.0, -34.0, -59.0, -58.0,
                null, null, null, from, to, null, null, null, null);

        assertNotNull(response);
        assertTrue(response.getPosts().isEmpty());
        verify(postRepository).findAll();
    }

    // ─── updatePost ───────────────────────────────────────────────────────────

    // Post y autor existen; ubicación ya existe en proximidad; no hay imágenes nuevas
    @Test
    void updatePost_ActualizacionExitosaSinImagenesNuevas_RetornaMensajeExito() {
        UpdatePostRequest updateRequest = validUpdateRequestMock();
        updateRequest.setKeepImageIds(new ArrayList<>());

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.singletonList(testLocation));
        when(postRepository.save(testPost)).thenReturn(testPost);

        ApiResponse response = postService.updatePost(1L, updateRequest, null);

        assertNotNull(response);
        assertEquals("Publicación actualizada con éxito", response.getMessage());
        verify(postRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(postRepository).save(testPost);
    }

    // Post tiene media=null → se inicializa como lista vacía antes de procesar
    @Test
    void updatePost_MediaNullEnPost_InicializaListaVaciaYProcede() {
        testPost.setMedia(null);
        UpdatePostRequest updateRequest = validUpdateRequestMock();
        updateRequest.setKeepImageIds(null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.singletonList(testLocation));
        when(postRepository.save(testPost)).thenReturn(testPost);

        ApiResponse response = postService.updatePost(1L, updateRequest, null);

        assertNotNull(response);
        assertEquals("Publicación actualizada con éxito", response.getMessage());
        assertNotNull(testPost.getMedia());
    }

    // Post tiene una imagen que no está en keepImageIds → se elimina de Cloudinary y de BD
    @Test
    void updatePost_EliminaImagenNoEnKeepIds_LlamaBorradoCloudinary() throws Exception {
        Media mediaExistente = new Media();
        mediaExistente.setId(10L);
        mediaExistente.setCloudinaryId("cloudinary_existing_id");
        mediaExistente.setUrl("http://example.com/old.jpg");
        mediaExistente.setType(MediaType.IMAGE);

        testPost.setMedia(new ArrayList<>(Collections.singletonList(mediaExistente)));

        UpdatePostRequest updateRequest = validUpdateRequestMock();
        updateRequest.setKeepImageIds(Collections.emptyList()); // no se conserva ninguna

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.singletonList(testLocation));
        when(cloudinaryService.delete("cloudinary_existing_id")).thenReturn(new HashMap<>());
        when(postRepository.save(testPost)).thenReturn(testPost);

        ApiResponse response = postService.updatePost(1L, updateRequest, null);

        assertNotNull(response);
        verify(cloudinaryService).delete("cloudinary_existing_id");
        assertTrue(testPost.getMedia().isEmpty());
    }

    // Imagen a eliminar con cloudinaryId en blanco → NO se llama a cloudinaryService.delete
    @Test
    void updatePost_ImagenAEliminarConCloudinaryIdBlanco_NoLlamaBorradoCloudinary() throws Exception {
        Media mediaConIdBlanco = new Media();
        mediaConIdBlanco.setId(20L);
        mediaConIdBlanco.setCloudinaryId("   ");
        mediaConIdBlanco.setUrl("http://example.com/blank.jpg");
        mediaConIdBlanco.setType(MediaType.IMAGE);

        testPost.setMedia(new ArrayList<>(Collections.singletonList(mediaConIdBlanco)));

        UpdatePostRequest updateRequest = validUpdateRequestMock();
        updateRequest.setKeepImageIds(Collections.emptyList());

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.singletonList(testLocation));
        when(postRepository.save(testPost)).thenReturn(testPost);

        postService.updatePost(1L, updateRequest, null);

        verify(cloudinaryService, never()).delete(any());
    }

    // Cloudinary lanza excepción al eliminar → se captura y el proceso continúa
    @Test
    void updatePost_CloudinaryDeleteFalla_ContinuaProceso() throws Exception {
        Media mediaExistente = new Media();
        mediaExistente.setId(10L);
        mediaExistente.setCloudinaryId("cloudinary_id_to_fail");
        mediaExistente.setUrl("http://example.com/fail.jpg");
        mediaExistente.setType(MediaType.IMAGE);

        testPost.setMedia(new ArrayList<>(Collections.singletonList(mediaExistente)));

        UpdatePostRequest updateRequest = validUpdateRequestMock();
        updateRequest.setKeepImageIds(Collections.emptyList());

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.singletonList(testLocation));
        when(cloudinaryService.delete("cloudinary_id_to_fail"))
                .thenThrow(new RuntimeException("Error de conexión Cloudinary"));
        when(postRepository.save(testPost)).thenReturn(testPost);

        // No debe lanzar excepción; el error de Cloudinary se absorbe
        ApiResponse response = postService.updatePost(1L, updateRequest, null);

        assertNotNull(response);
        assertEquals("Publicación actualizada con éxito", response.getMessage());
    }

    // keepImageIds vacío + 1 imagen nueva (total = 1 <= 5) → se sube exitosamente
    @Test
    void updatePost_ConNuevaImagenYLimiteNoSuperado_SubeACloudinary() throws Exception {
        Map<String, Object> uploadResult = validUploadResultMock();

        UpdatePostRequest updateRequest = validUpdateRequestMock();
        updateRequest.setKeepImageIds(Collections.emptyList());

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.singletonList(testLocation));
        when(cloudinaryService.upload(mockImage)).thenReturn(uploadResult);
        when(postRepository.save(testPost)).thenReturn(testPost);

        ApiResponse response = postService.updatePost(1L, updateRequest, Collections.singletonList(mockImage));

        assertNotNull(response);
        assertEquals("Publicación actualizada con éxito", response.getMessage());
        verify(cloudinaryService).upload(mockImage);
    }

    // keepImageIds tiene 4 ids + 2 nuevas imágenes = 6 > 5 → excepción
    @Test
    void updatePost_NuevasImagenesSuperanLimite_LanzaIllegalArgumentException() throws Exception {
        List<Long> keepIds = Arrays.asList(1L, 2L, 3L, 4L);

        UpdatePostRequest updateRequest = validUpdateRequestMock();
        updateRequest.setKeepImageIds(keepIds);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.singletonList(testLocation));

        List<MultipartFile> tooManyImages = Arrays.asList(mockImage, mockImage);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> postService.updatePost(1L, updateRequest, tooManyImages));

        assertEquals("Máximo 5 imágenes permitidas por publicación", ex.getMessage());
    }

    // Cloudinary falla al subir nueva imagen → se relanza como RuntimeException
    @Test
    void updatePost_CloudinaryUploadFallaEnNuevaImagen_LanzaRuntimeException() throws Exception {
        UpdatePostRequest updateRequest = validUpdateRequestMock();
        updateRequest.setKeepImageIds(Collections.emptyList());

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(-34.6118, -58.3960))
                .thenReturn(Collections.singletonList(testLocation));
        when(cloudinaryService.upload(mockImage)).thenThrow(new RuntimeException("Error al subir"));

        List<MultipartFile> images = Collections.singletonList(mockImage);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> postService.updatePost(1L, updateRequest, images));

        assertTrue(ex.getMessage().contains("Error al subir imagen"));
    }

    // Post con id=1 no existe → excepción inmediata
    @Test
    void updatePost_PostNoEncontrado_LanzaResourceNotFoundException() {
        UpdatePostRequest updateRequest = validUpdateRequestMock();
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.updatePost(1L, updateRequest, null));

        assertEquals("Publicación no encontrada con id: 1", ex.getMessage());
    }

    // Post existe pero el autor del request no existe → excepción
    @Test
    void updatePost_AutorNoEncontrado_LanzaResourceNotFoundException() {
        UpdatePostRequest updateRequest = validUpdateRequestMock();
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.updatePost(1L, updateRequest, null));

        assertEquals("Usuario no encontrado con id: 1", ex.getMessage());
    }

    // ─── deletePost ───────────────────────────────────────────────────────────

    // Post activo → cambia status a DELETED (eliminación lógica, no física)
    @Test
    void deletePost_PostExistente_EliminacionLogicaYRetornaMensajeExito() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(testPost)).thenReturn(testPost);

        ApiResponse response = postService.deletePost(1L);

        assertNotNull(response);
        assertEquals("Publicación eliminada con éxito", response.getMessage());
        assertEquals(PostStatus.DELETED, testPost.getStatus());
        verify(postRepository).save(testPost);
        verify(postRepository, never()).delete(testPost);
    }

    // Post con id=1 no existe → excepción con mensaje exacto
    @Test
    void deletePost_PostNoEncontrado_LanzaResourceNotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.deletePost(1L));

        assertEquals("Publicación no encontrada con id: 1", ex.getMessage());
    }

    // ─── deletePostMedia ──────────────────────────────────────────────────────

    // Post tiene imagen con cloudinaryId válido → se borra de Cloudinary y luego de BD
    @Test
    void deletePostMedia_ImagenConCloudinaryId_EliminaDeCloudinaryYBD() throws Exception {
        Media media = new Media();
        media.setId(1L);
        media.setCloudinaryId("test_cloudinary_123");
        media.setType(MediaType.IMAGE);
        media.setUrl("http://example.com/img.jpg");

        testPost.setMedia(new ArrayList<>(Collections.singletonList(media)));

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(cloudinaryService.delete("test_cloudinary_123")).thenReturn(new HashMap<>());
        when(postRepository.save(testPost)).thenReturn(testPost);

        ApiResponse response = postService.deletePostMedia(1L, 1L);

        assertNotNull(response);
        assertEquals("Imagen eliminada con éxito", response.getMessage());
        verify(cloudinaryService).delete("test_cloudinary_123");
        verify(postRepository).save(testPost);
        assertTrue(testPost.getMedia().isEmpty());
    }

    // Media sin cloudinaryId (null) → no se llama a cloudinaryService.delete
    @Test
    void deletePostMedia_ImagenSinCloudinaryId_NoLlamaCloudinaryDelete() throws Exception {
        Media media = new Media();
        media.setId(1L);
        media.setCloudinaryId(null);
        media.setType(MediaType.IMAGE);
        media.setUrl("http://example.com/img.jpg");

        testPost.setMedia(new ArrayList<>(Collections.singletonList(media)));

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(testPost)).thenReturn(testPost);

        ApiResponse response = postService.deletePostMedia(1L, 1L);

        assertNotNull(response);
        assertEquals("Imagen eliminada con éxito", response.getMessage());
        verify(cloudinaryService, never()).delete(any());
    }

    // Cloudinary lanza excepción al borrar → se captura y se elimina de BD igualmente
    @Test
    void deletePostMedia_CloudinaryFallaAlEliminar_ContinuaYEliminaDeDB() throws Exception {
        Media media = new Media();
        media.setId(1L);
        media.setCloudinaryId("cloudinary_fail_id");
        media.setType(MediaType.IMAGE);
        media.setUrl("http://example.com/img.jpg");

        testPost.setMedia(new ArrayList<>(Collections.singletonList(media)));

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(cloudinaryService.delete("cloudinary_fail_id"))
                .thenThrow(new RuntimeException("Error de Cloudinary"));
        when(postRepository.save(testPost)).thenReturn(testPost);

        ApiResponse response = postService.deletePostMedia(1L, 1L);

        assertNotNull(response);
        assertEquals("Imagen eliminada con éxito", response.getMessage());
        assertTrue(testPost.getMedia().isEmpty());
    }

    // Post con id=1 no existe → excepción inmediata
    @Test
    void deletePostMedia_PostNoEncontrado_LanzaResourceNotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.deletePostMedia(1L, 1L));

        assertEquals("Publicación no encontrada con id: 1", ex.getMessage());
    }

    // Post existe pero no contiene la imagen con id=99 → excepción
    @Test
    void deletePostMedia_MediaNoEncontrada_LanzaResourceNotFoundException() {
        testPost.setMedia(new ArrayList<>());
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.deletePostMedia(1L, 99L));

        assertEquals("Imagen no encontrada con id: 99", ex.getMessage());
    }

    // ─── addPostMedia ─────────────────────────────────────────────────────────

    // Post ya tiene 2 imágenes; se agregan 2 más (total 4 <= 5) → éxito
    @Test
    void addPostMedia_PostConMediaExistente_AgregaNuevosUrls() {
        Media mediaExistente1 = new Media();
        mediaExistente1.setId(1L);
        Media mediaExistente2 = new Media();
        mediaExistente2.setId(2L);
        testPost.setMedia(new ArrayList<>(Arrays.asList(mediaExistente1, mediaExistente2)));

        List<String> newUrls = Arrays.asList("http://example.com/new1.jpg", "http://example.com/new2.jpg");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(testPost)).thenReturn(testPost);

        ApiResponse response = postService.addPostMedia(1L, newUrls);

        assertNotNull(response);
        assertEquals("Imágenes agregadas con éxito", response.getMessage());
        assertEquals(4, testPost.getMedia().size());
        verify(postRepository).save(testPost);
    }

    // Post con getMedia() == null → se inicializa internamente y agrega los urls
    @Test
    void addPostMedia_PostSinMedia_InicializaListaYAgregaUrls() {
        testPost.setMedia(null);
        List<String> newUrls = Collections.singletonList("http://example.com/img.jpg");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(testPost)).thenReturn(testPost);

        ApiResponse response = postService.addPostMedia(1L, newUrls);

        assertNotNull(response);
        assertEquals("Imágenes agregadas con éxito", response.getMessage());
        assertNotNull(testPost.getMedia());
        assertEquals(1, testPost.getMedia().size());
        assertEquals("http://example.com/img.jpg", testPost.getMedia().get(0).getUrl());
        assertEquals(MediaType.IMAGE, testPost.getMedia().get(0).getType());
    }

    // 4 imágenes existentes + 2 nuevas = 6 > 5 → excepción antes de guardar
    @Test
    void addPostMedia_SuperaLimiteDecinco_LanzaIllegalArgumentException() {
        Media m1 = new Media(); m1.setId(1L);
        Media m2 = new Media(); m2.setId(2L);
        Media m3 = new Media(); m3.setId(3L);
        Media m4 = new Media(); m4.setId(4L);
        testPost.setMedia(new ArrayList<>(Arrays.asList(m1, m2, m3, m4)));

        List<String> newUrls = Arrays.asList("http://example.com/img5.jpg", "http://example.com/img6.jpg");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> postService.addPostMedia(1L, newUrls));

        assertEquals("No se pueden agregar más imágenes. Límite máximo: 5 imágenes por publicación",
                ex.getMessage());
        verify(postRepository, never()).save(any());
    }

    // Post con id=1 no existe → excepción inmediata
    @Test
    void addPostMedia_PostNoEncontrado_LanzaResourceNotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        List<String> urls = Collections.singletonList("http://url.com/img.jpg");
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.addPostMedia(1L, urls));

        assertEquals("Publicación no encontrada con id: 1", ex.getMessage());
    }

    // ─── helpers privados de test ─────────────────────────────────────────────

    private UpdatePostRequest validUpdateRequestMock() {
        UpdatePostRequest req = new UpdatePostRequest();
        req.setTitle("Titulo Actualizado");
        req.setContent("Contenido actualizado con suficiente texto");
        req.setDate(LocalDate.of(2024, 6, 15));
        req.setCategory(Category.INFORMATION);
        req.setAuthorId(1L);
        req.setLatitude(-34.6118);
        req.setLongitude(-58.3960);
        req.setAddress("Dirección Actualizada");
        req.setIsByIA(false);
        req.setKeepImageIds(new ArrayList<>());
        return req;
    }
}
