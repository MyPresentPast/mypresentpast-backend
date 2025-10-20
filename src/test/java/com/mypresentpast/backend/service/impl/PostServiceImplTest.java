package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.CreatePostRequest;
import com.mypresentpast.backend.dto.request.UpdatePostRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.MapResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import com.mypresentpast.backend.enums.Category;
import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.*;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        // Crear User normal
        testUser = new User();
        testUser.setId(1L);
        testUser.setProfileUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole(UserRole.NORMAL);

        // Crear User institución
        institutionUser = new User();
        institutionUser.setId(2L);
        institutionUser.setProfileUsername("institution");
        institutionUser.setPassword("password");
        institutionUser.setRole(UserRole.INSTITUTION);

        // Crear Location
        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setAddress("Test Address");
        testLocation.setLatitude(-34.6118);
        testLocation.setLongitude(-58.3960);

        // Crear Post
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setDate(LocalDate.now());
        testPost.setPostedAt(LocalDate.now());
        testPost.setCategory(Category.STORY);
        testPost.setIsByIA(false);
        testPost.setStatus(PostStatus.ACTIVE);
        testPost.setAuthor(testUser);
        testPost.setLocation(testLocation);
        testPost.setMedia(new ArrayList<>());

        // Crear CreatePostRequest
        createRequest = new CreatePostRequest();
        createRequest.setTitle("New Test Post");
        createRequest.setContent("New test content");
        createRequest.setDate(LocalDate.now());
        createRequest.setCategory(Category.STORY);
        createRequest.setIsByIA(false);
        createRequest.setAuthorId(1L);
        createRequest.setLatitude(-34.6118);
        createRequest.setLongitude(-58.3960);
        createRequest.setAddress("Buenos Aires, Argentina");
    }

    @Test
    void createPost_Success_WithoutImages() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(anyDouble(), anyDouble()))
            .thenReturn(Collections.emptyList());
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        ApiResponse response = postService.createPost(createRequest, null);

        // Then
        assertNotNull(response);
        assertEquals("Publicación creada con éxito", response.getMessage());
        verify(userRepository).findById(1L);
        verify(locationRepository).findLocationsByProximity(-34.6118, -58.3960);
        verify(locationRepository).save(any(Location.class));
        verify(postRepository).save(any(Post.class));
        verify(cloudinaryService, never()).upload(any());
    }

    @Test
    void createPost_Success_WithImages() throws Exception {
        // Given
        List<MultipartFile> images = Arrays.asList(mockImage);
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "http://test.com/image.jpg");
        uploadResult.put("public_id", "test123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(anyDouble(), anyDouble()))
            .thenReturn(Collections.emptyList());
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(cloudinaryService.upload(any(MultipartFile.class))).thenReturn(uploadResult);
        when(mediaRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // When
        ApiResponse response = postService.createPost(createRequest, images);

        // Then
        assertNotNull(response);
        assertEquals("Publicación creada con éxito", response.getMessage());
        verify(cloudinaryService).upload(mockImage);
        verify(mediaRepository).saveAll(anyList());
    }

    @Test
    void createPost_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> postService.createPost(createRequest, null)
        );

        assertEquals("Usuario no encontrado con id: 1", exception.getMessage());
    }

    @Test
    void createPost_WithTooManyImages_ThrowsException() {
        // Given
        List<MultipartFile> tooManyImages = Arrays.asList(
            mockImage, mockImage, mockImage, mockImage, mockImage, mockImage // 6 imágenes
        );

        // Configurar todos los mocks necesarios hasta llegar a la validación de imágenes
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(anyDouble(), anyDouble()))
            .thenReturn(Collections.emptyList());
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> postService.createPost(createRequest, tooManyImages)
        );

        assertEquals("Máximo 5 imágenes permitidas por publicación", exception.getMessage());
    }

    @Test
    void getPostById_Success() {
        // Given
        when(postRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testPost));
        when(likeService.getTotalLikes(1L)).thenReturn(5L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(true);
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);

        // When
        PostResponse response = postService.getPostById(1L);

        // Then
        assertNotNull(response);
        assertEquals("Test Post", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals(testUser.getProfileUsername(), response.getAuthor().getName());
        assertEquals(5L, response.getTotalLikes());
        assertEquals(true, response.getIsLiked());
        verify(postRepository).findByIdWithRelations(1L);
        verify(likeService).getTotalLikes(1L);
        verify(likeService).isLikedByCurrentUser(1L);
        verify(verificationQueryService).isPostVerified(testPost);
        verify(verificationQueryService).getExternalVerifier(1L);
    }

    @Test
    void getPostById_NotFound_ThrowsException() {
        // Given
        when(postRepository.findByIdWithRelations(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> postService.getPostById(1L)
        );

        assertEquals("Publicación no encontrada con id: 1", exception.getMessage());
        verify(postRepository).findByIdWithRelations(1L);
    }

    @Test
    void getPostsByUser_Success() {
        // Given
        List<Post> userPosts = Arrays.asList(testPost);
        when(postRepository.findByAuthorIdAndStatus(1L, PostStatus.ACTIVE)).thenReturn(userPosts);
        when(likeService.getTotalLikes(1L)).thenReturn(3L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(false);
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);

        // When
        List<PostResponse> responses = postService.getPostsByUser(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Post", responses.get(0).getTitle());
        verify(postRepository).findByAuthorIdAndStatus(1L, PostStatus.ACTIVE);
    }

    @Test
    void getPostsByUser_NoPostsFound_ThrowsException() {
        // Given
        when(postRepository.findByAuthorIdAndStatus(1L, PostStatus.ACTIVE)).thenReturn(Collections.emptyList());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> postService.getPostsByUser(1L)
        );

        assertEquals("No hay publicaciones disponibles para mostrar", exception.getMessage());
    }

    @Test
    void getMapData_Success_WithAllFilters() {
        // Given
        List<Post> mockPosts = Arrays.asList(testPost);
        List<String> categoryStrings = Arrays.asList("STORY");
        List<Long> userIds = Arrays.asList(1L);
        LocalDate testDate = LocalDate.now();

        when(postRepository.findPostsInAreaWithMultipleFilters(
            eq(-35.0), eq(-34.0), eq(-59.0), eq(-58.0),
            eq(categoryStrings), eq(testDate), eq(testDate), eq(false), eq(userIds)))
            .thenReturn(mockPosts);
        when(likeService.getTotalLikes(1L)).thenReturn(8L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(true);
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(true);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);

        // When
        MapResponse response = postService.getMapData(
            -35.0, -34.0, -59.0, -58.0, "STORY", Arrays.asList("STORY"), testDate, null, null, true, false, 1L, Arrays.asList(1L)
        );

        // Then
        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        assertEquals("Test Post", response.getPosts().get(0).getTitle());
        assertEquals(8L, response.getPosts().get(0).getTotalLikes());
        assertEquals(true, response.getPosts().get(0).getIsLiked());
        verify(postRepository).findPostsInAreaWithMultipleFilters(
            -35.0, -34.0, -59.0, -58.0, categoryStrings, testDate, testDate, false, userIds
        );
    }

    @Test
    void getMapData_Success_WithMinimalParams() {
        // Given
        List<Post> mockPosts = Arrays.asList(testPost);
        when(postRepository.findPostsInAreaWithMultipleFilters(
            eq(-35.0), eq(-34.0), eq(-59.0), eq(-58.0),
            anyList(), eq(null), eq(null), eq(null), anyList()))
            .thenReturn(mockPosts);
        when(likeService.getTotalLikes(1L)).thenReturn(2L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(false);
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);

        // When
        MapResponse response = postService.getMapData(
            -35.0, -34.0, -59.0, -58.0, null, null, null, null, null, null, null, null, null
        );

        // Then
        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        assertEquals(2L, response.getPosts().get(0).getTotalLikes());
        assertEquals(false, response.getPosts().get(0).getIsLiked());
    }

    @Test
    void getMapData_WithVerifiedFilter() {
        // Given
        List<Post> mockPosts = Arrays.asList(testPost);
        when(postRepository.findPostsInAreaWithMultipleFilters(
            eq(-35.0), eq(-34.0), eq(-59.0), eq(-58.0),
            anyList(), eq(null), eq(null), eq(null), anyList()))
            .thenReturn(mockPosts);
        when(likeService.getTotalLikes(1L)).thenReturn(12L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(true);
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(true);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(institutionUser);

        // When
        MapResponse response = postService.getMapData(
            -35.0, -34.0, -59.0, -58.0, null, null, null, null, null, true, null, null, null
        );

        // Then
        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        assertEquals(12L, response.getPosts().get(0).getTotalLikes());
        assertEquals(true, response.getPosts().get(0).getIsLiked());
        assertEquals(true, response.getPosts().get(0).getIsVerified());
    }

    @Test
    void getMapData_FilterByIA() {
        // Given
        List<Post> mockPosts = Arrays.asList(testPost);
        when(postRepository.findPostsInAreaWithMultipleFilters(
            eq(-35.0), eq(-34.0), eq(-59.0), eq(-58.0),
            anyList(), eq(null), eq(null), eq(false), anyList()))
            .thenReturn(mockPosts);
        when(likeService.getTotalLikes(1L)).thenReturn(7L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(false);
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);

        // When
        MapResponse response = postService.getMapData(
            -35.0, -34.0, -59.0, -58.0, null, null, null, null, null, null, false, null, null
        );

        // Then
        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        assertEquals(7L, response.getPosts().get(0).getTotalLikes());
        assertEquals(false, response.getPosts().get(0).getIsLiked());
    }

    @Test
    void getMapData_FilterByUser() {
        // Given
        List<Post> mockPosts = Arrays.asList(testPost);
        List<Long> userIds = Arrays.asList(1L);

        when(postRepository.findPostsInAreaWithMultipleFilters(
            eq(-35.0), eq(-34.0), eq(-59.0), eq(-58.0),
            anyList(), eq(null), eq(null), eq(null), eq(userIds)))
            .thenReturn(mockPosts);
        when(likeService.getTotalLikes(1L)).thenReturn(15L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(true);
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);

        // When
        MapResponse response = postService.getMapData(
            -35.0, -34.0, -59.0, -58.0, null, null, null, null, null, null, null, 1L, Arrays.asList(1L)
        );

        // Then
        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        assertEquals("Test Post", response.getPosts().get(0).getTitle());
        assertEquals(15L, response.getPosts().get(0).getTotalLikes());
        assertEquals(true, response.getPosts().get(0).getIsLiked());
    }

    @Test
    void getLikedPostsByCurrentUser_Success() {
        // Given
        List<Post> likedPosts = Arrays.asList(testPost);
        when(postRepository.findLikedPostsByUserId(1L)).thenReturn(likedPosts);
        when(likeService.getTotalLikes(1L)).thenReturn(10L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(true);
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);

        // When & Then - Mock SecurityUtils
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            
            List<PostResponse> response = postService.getLikedPostsByCurrentUser();

            assertNotNull(response);
            assertEquals(1, response.size());
            assertEquals("Test Post", response.get(0).getTitle());
            assertEquals(10L, response.get(0).getTotalLikes());
            assertEquals(true, response.get(0).getIsLiked());
            verify(postRepository).findLikedPostsByUserId(1L);
        }
    }

    @Test
    void getLikedPostsByCurrentUser_NoLikedPosts() {
        // Given
        when(postRepository.findLikedPostsByUserId(1L)).thenReturn(new ArrayList<>());

        // When & Then - Mock SecurityUtils
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            
            List<PostResponse> response = postService.getLikedPostsByCurrentUser();

            assertNotNull(response);
            assertTrue(response.isEmpty());
            verify(postRepository).findLikedPostsByUserId(1L);
        }
    }

    @Test
    void getRandomPost_Success() {
        // Given
        List<Post> activePosts = Arrays.asList(testPost);
        when(postRepository.findByStatusAndLocationIsNotNull(PostStatus.ACTIVE))
            .thenReturn(activePosts);
        when(likeService.getTotalLikes(1L)).thenReturn(3L);
        when(likeService.isLikedByCurrentUser(1L)).thenReturn(false);
        when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
        when(verificationQueryService.getExternalVerifier(1L)).thenReturn(null);

        // When
        PostResponse response = postService.getRandomPost();

        // Then
        assertNotNull(response);
        assertEquals("Test Post", response.getTitle());
        assertEquals(3L, response.getTotalLikes());
        assertEquals(false, response.getIsLiked());
        verify(postRepository).findByStatusAndLocationIsNotNull(PostStatus.ACTIVE);
        verify(likeService).getTotalLikes(1L);
        verify(likeService).isLikedByCurrentUser(1L);
    }

    @Test
    void getRandomPost_NoPosts_ThrowsException() {
        // Given
        when(postRepository.findByStatusAndLocationIsNotNull(PostStatus.ACTIVE))
            .thenReturn(Collections.emptyList());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> postService.getRandomPost()
        );

        assertEquals("No hay publicaciones disponibles para mostrar", exception.getMessage());
    }

    @Test
    void updatePost_Success() {
        // Given
        UpdatePostRequest updateRequest = new UpdatePostRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated Content");
        updateRequest.setCategory(Category.INFORMATION);
        updateRequest.setAuthorId(1L);
        updateRequest.setLatitude(-34.6118);
        updateRequest.setLongitude(-58.3960);
        updateRequest.setAddress("Updated Address");
        updateRequest.setDate(LocalDate.now());
        updateRequest.setIsByIA(false);
        updateRequest.setKeepImageIds(new ArrayList<>());

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(anyDouble(), anyDouble()))
            .thenReturn(Arrays.asList(testLocation));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        ApiResponse response = postService.updatePost(1L, updateRequest, null);

        // Then
        assertNotNull(response);
        assertEquals("Publicación actualizada con éxito", response.getMessage());
        verify(postRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(postRepository).save(testPost);
    }

    @Test
    void updatePost_NotFound_ThrowsException() {
        // Given
        UpdatePostRequest updateRequest = new UpdatePostRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setAuthorId(1L);
        updateRequest.setLatitude(-34.6118);
        updateRequest.setLongitude(-58.3960);
        updateRequest.setAddress("Updated Address");

        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> postService.updatePost(1L, updateRequest, null)
        );

        assertEquals("Publicación no encontrada con id: 1", exception.getMessage());
    }

    @Test
    void deletePost_Success() {
        // Given - deletePost hace eliminación LÓGICA, no física
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        ApiResponse response = postService.deletePost(1L);

        // Then
        assertNotNull(response);
        assertEquals("Publicación eliminada con éxito", response.getMessage());
        verify(postRepository).save(testPost);
        verify(postRepository, never()).delete(testPost);

        // Verificar que el status cambió a DELETED
        assertEquals(PostStatus.DELETED, testPost.getStatus());
    }

    @Test
    void deletePost_NotFound_ThrowsException() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> postService.deletePost(1L)
        );

        assertEquals("Publicación no encontrada con id: 1", exception.getMessage());
    }

    @Test
    void deletePostMedia_Success() {
        // Given
        Media testMedia = new Media();
        testMedia.setId(1L);
        testMedia.setCloudinaryId("test123");

        testPost.setMedia(new ArrayList<>(Arrays.asList(testMedia)));

        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("result", "ok");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(cloudinaryService.delete("test123")).thenReturn(deleteResult);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        ApiResponse response = postService.deletePostMedia(1L, 1L);

        // Then
        assertNotNull(response);
        assertEquals("Imagen eliminada con éxito", response.getMessage());
        verify(cloudinaryService).delete("test123");
        verify(postRepository).save(testPost);
    }

    @Test
    void addPostMedia_Success() {
        // Given
        List<String> imageUrls = Arrays.asList("http://test.com/image1.jpg", "http://test.com/image2.jpg");
        testPost.setMedia(new ArrayList<>());

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        ApiResponse response = postService.addPostMedia(1L, imageUrls);

        // Then
        assertNotNull(response);
        assertEquals("Imágenes agregadas con éxito", response.getMessage());
        verify(postRepository).save(testPost);
    }

    @Test
    void addPostMedia_ExceedsLimit_ThrowsException() {
        // Given
        List<String> imageUrls = Arrays.asList("url1", "url2");

        // Crear media existente manualmente
        Media media1 = new Media();
        Media media2 = new Media();
        Media media3 = new Media();
        Media media4 = new Media();

        List<Media> existingMedia = Arrays.asList(media1, media2, media3, media4); // 4 existentes
        testPost.setMedia(existingMedia);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> postService.addPostMedia(1L, imageUrls)
        );

        assertEquals("No se pueden agregar más imágenes. Límite máximo: 5 imágenes por publicación",
                     exception.getMessage());
    }
}