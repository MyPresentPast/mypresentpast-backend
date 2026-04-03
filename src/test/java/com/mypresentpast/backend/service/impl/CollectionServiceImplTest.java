package com.mypresentpast.backend.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mypresentpast.backend.dto.request.CreateCollectionAndSavePostRequest;
import com.mypresentpast.backend.dto.request.CreateCollectionRequest;
import com.mypresentpast.backend.dto.request.UpdateCollectionRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.CollectionSummaryResponse;
import com.mypresentpast.backend.dto.response.PostCollectionStatusResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import com.mypresentpast.backend.enums.Category;
import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Collection;
import com.mypresentpast.backend.model.CollectionPost;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.CollectionPostRepository;
import com.mypresentpast.backend.repository.CollectionRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.PostVerificationQueryService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.mypresentpast.backend.enums.MediaType;
import com.mypresentpast.backend.model.Location;
import com.mypresentpast.backend.model.Media;

@ExtendWith(MockitoExtension.class)
class CollectionServiceImplTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private CollectionPostRepository collectionPostRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostVerificationQueryService verificationQueryService;

    @InjectMocks
    private CollectionServiceImpl collectionService;

    private User testUser;
    private Post testPost;
    private Collection testCollection;
    private CollectionPost testCollectionPost;

    @BeforeEach
    void setUp() {
        // Crear User de prueba
        testUser = new User();
        testUser.setId(1L);
        testUser.setProfileUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.NORMAL);

        // Crear Post de prueba
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setDate(LocalDate.now());
        testPost.setPostedAt(LocalDate.now());
        testPost.setCategory(Category.STORY);
        testPost.setStatus(PostStatus.ACTIVE);
        testPost.setAuthor(testUser);
        testPost.setMedia(new ArrayList<>());

        // Crear Collection de prueba
        testCollection = new Collection();
        testCollection.setId(1L);
        testCollection.setName("Test Collection");
        testCollection.setDescription("Test Description");
        testCollection.setAuthor(testUser);
        testCollection.setCreatedAt(LocalDateTime.now());
        testCollection.setUpdatedAt(LocalDateTime.now());

        // Crear CollectionPost de prueba
        testCollectionPost = new CollectionPost();
        testCollectionPost.setId(1L);
        testCollectionPost.setCollection(testCollection);
        testCollectionPost.setPost(testPost);
        testCollectionPost.setAddedAt(LocalDateTime.now());
    }

    @Test
    void getMyCollections_ShouldReturnCollectionSummaries() {
        // Given
        Long userId = 1L;
        Object[] mockResult = {testCollection, 3L}; // Collection y post count
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(mockResult);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.findCollectionsWithPostCountByAuthorId(userId))
                .thenReturn(mockResults);

            // When
            List<CollectionSummaryResponse> result = collectionService.getMyCollections();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Collection", result.get(0).getName());
            assertEquals("Test Description", result.get(0).getDescription());
            assertEquals(3, result.get(0).getPostCount());
        }
    }

    @Test
    void createCollection_ShouldSucceed_WhenValidRequest() {
        // Given
        Long userId = 1L;
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("New Collection");
        request.setDescription("New Description");

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.countByAuthorId(userId)).thenReturn(5L);
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorId(anyString(), anyLong())).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(collectionRepository.save(any(Collection.class))).thenReturn(testCollection);

            // When
            ApiResponse result = collectionService.createCollection(request);

            // Then
            assertNotNull(result);
            assertEquals("Colección creada con éxito", result.getMessage());
            verify(collectionRepository).save(any(Collection.class));
        }
    }

    @Test
    void createCollection_ShouldThrowException_WhenMaxCollectionsReached() {
        // Given
        Long userId = 1L;
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("New Collection");

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.countByAuthorId(userId)).thenReturn(20L); // Máximo alcanzado

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> collectionService.createCollection(request));
            
            assertEquals("Has alcanzado el límite máximo de 20 colecciones", exception.getMessage());
        }
    }

    @Test
    void createCollection_ShouldThrowException_WhenDuplicateName() {
        // Given
        Long userId = 1L;
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("Existing Collection");

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.countByAuthorId(userId)).thenReturn(5L);
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorId("Existing Collection", userId)).thenReturn(true);

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> collectionService.createCollection(request));
            
            assertEquals("Ya tienes una colección con ese nombre", exception.getMessage());
        }
    }

    @Test
    void updateCollection_ShouldSucceed_WhenValidRequest() {
        // Given
        Long userId = 1L;
        Long collectionId = 1L;
        UpdateCollectionRequest request = new UpdateCollectionRequest();
        request.setName("Updated Collection");
        request.setDescription("Updated Description");

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.findByIdAndAuthorId(collectionId, userId))
                .thenReturn(Optional.of(testCollection));
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorIdAndIdNot(anyString(), anyLong(), anyLong()))
                .thenReturn(false);
            when(collectionRepository.save(any(Collection.class))).thenReturn(testCollection);

            // When
            ApiResponse result = collectionService.updateCollection(collectionId, request);

            // Then
            assertNotNull(result);
            assertEquals("Colección actualizada con éxito", result.getMessage());
            verify(collectionRepository).save(testCollection);
        }
    }

    @Test
    void deleteCollection_ShouldSucceed_WhenCollectionExists() {
        // Given
        Long userId = 1L;
        Long collectionId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.findByIdAndAuthorId(collectionId, userId))
                .thenReturn(Optional.of(testCollection));

            // When
            collectionService.deleteCollection(collectionId);

            // Then
            verify(collectionRepository).delete(testCollection);
        }
    }

    @Test
    void deleteCollection_ShouldThrowException_WhenCollectionNotFound() {
        // Given
        Long userId = 1L;
        Long collectionId = 999L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.findByIdAndAuthorId(collectionId, userId))
                .thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> collectionService.deleteCollection(collectionId));
            
            assertEquals("Colección no encontrada", exception.getMessage());
        }
    }

    @Test
    void getCollectionPosts_ShouldReturnPosts() {
        // Given
        Long userId = 1L;
        Long collectionId = 1L;
        List<CollectionPost> collectionPosts = List.of(testCollectionPost);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.findByIdAndAuthorId(collectionId, userId))
                .thenReturn(Optional.of(testCollection));
            when(collectionPostRepository.findByCollectionIdOrderByAddedAtDesc(collectionId))
                .thenReturn(collectionPosts);
            when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
            when(verificationQueryService.getExternalVerifier(testPost.getId())).thenReturn(null);

            // When
            List<PostResponse> result = collectionService.getCollectionPosts(collectionId);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Test
    void addPostToCollection_ShouldSucceed_WhenValidRequest() {
        // Given
        Long userId = 1L;
        Long collectionId = 1L;
        Long postId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.findByIdAndAuthorId(collectionId, userId))
                .thenReturn(Optional.of(testCollection));
            when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
            when(collectionPostRepository.existsByCollectionIdAndPostId(collectionId, postId))
                .thenReturn(false);
            when(collectionPostRepository.countByCollectionId(collectionId)).thenReturn(5L);
            when(collectionPostRepository.save(any(CollectionPost.class))).thenReturn(testCollectionPost);

            // When
            ApiResponse result = collectionService.addPostToCollection(collectionId, postId);

            // Then
            assertNotNull(result);
            assertEquals("Post agregado a la colección", result.getMessage());
            verify(collectionPostRepository).save(any(CollectionPost.class));
        }
    }

    @Test
    void addPostToCollection_ShouldThrowException_WhenPostAlreadyInCollection() {
        // Given
        Long userId = 1L;
        Long collectionId = 1L;
        Long postId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.findByIdAndAuthorId(collectionId, userId))
                .thenReturn(Optional.of(testCollection));
            when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
            when(collectionPostRepository.existsByCollectionIdAndPostId(collectionId, postId))
                .thenReturn(true);

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> collectionService.addPostToCollection(collectionId, postId));
            
            assertEquals("El post ya está en esta colección", exception.getMessage());
        }
    }

    @Test
    void addPostToCollection_ShouldThrowException_WhenMaxPostsReached() {
        // Given
        Long userId = 1L;
        Long collectionId = 1L;
        Long postId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.findByIdAndAuthorId(collectionId, userId))
                .thenReturn(Optional.of(testCollection));
            when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
            when(collectionPostRepository.existsByCollectionIdAndPostId(collectionId, postId))
                .thenReturn(false);
            when(collectionPostRepository.countByCollectionId(collectionId)).thenReturn(20L); // Máximo

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> collectionService.addPostToCollection(collectionId, postId));
            
            assertEquals("La colección ha alcanzado el límite máximo de 20 posts", exception.getMessage());
        }
    }

    @Test
    void removePostFromCollection_ShouldSucceed_WhenPostInCollection() {
        // Given
        Long userId = 1L;
        Long collectionId = 1L;
        Long postId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.findByIdAndAuthorId(collectionId, userId))
                .thenReturn(Optional.of(testCollection));
            when(collectionPostRepository.findByCollectionIdAndPostId(collectionId, postId))
                .thenReturn(Optional.of(testCollectionPost));

            // When
            ApiResponse result = collectionService.removePostFromCollection(collectionId, postId);

            // Then
            assertNotNull(result);
            assertEquals("Post eliminado de la colección", result.getMessage());
            verify(collectionPostRepository).delete(testCollectionPost);
        }
    }

    @Test
    void createCollectionAndSavePost_ShouldSucceed_WhenValidRequest() {
        // Given
        Long userId = 1L;
        CreateCollectionAndSavePostRequest request = new CreateCollectionAndSavePostRequest();
        request.setName("New Collection");
        request.setDescription("New Description");
        request.setPostId(1L);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity = 
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            
            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            
            when(collectionRepository.countByAuthorId(userId)).thenReturn(5L);
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorId(anyString(), anyLong())).thenReturn(false);
            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(collectionRepository.save(any(Collection.class))).thenReturn(testCollection);
            when(collectionPostRepository.save(any(CollectionPost.class))).thenReturn(testCollectionPost);

            // When
            ApiResponse result = collectionService.createCollectionAndSavePost(request);

            // Then
            assertNotNull(result);
            assertEquals("Colección creada y post guardado con éxito", result.getMessage());
            verify(collectionRepository).save(any(Collection.class));
            verify(collectionPostRepository).save(any(CollectionPost.class));
        }
    }

    @Test
    void getPostCollectionStatus_ShouldReturnStatus() {
        // Given
        Long userId = 1L;
        Long postId = 1L;
        List<CollectionPost> collectionPosts = List.of(testCollectionPost);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);

            when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
            when(collectionPostRepository.findByPostIdAndCollectionAuthorId(postId, userId))
                .thenReturn(collectionPosts);

            // When
            PostCollectionStatusResponse result = collectionService.getPostCollectionStatus(postId);

            // Then
            assertNotNull(result);
            assertEquals(postId, result.getPostId());
            assertEquals(1, result.getCollectionsContaining().size());
            assertEquals("Test Collection", result.getCollectionsContaining().get(0).getCollectionName());
        }
    }

    // Verifica que updateCollection lanza ResourceNotFoundException cuando la colección no pertenece al usuario.
    @Test
    void updateCollection_CollectionNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;
        UpdateCollectionRequest request = new UpdateCollectionRequest();
        request.setName("Updated");

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(99L, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> collectionService.updateCollection(99L, request));
        }
    }

    // Verifica que updateCollection lanza BadRequestException cuando el nuevo nombre ya existe en otra colección.
    @Test
    void updateCollection_DuplicateName_ThrowsBadRequestException() {
        Long userId = 1L;
        UpdateCollectionRequest request = new UpdateCollectionRequest();
        request.setName("Other Collection");

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(1L, userId)).thenReturn(Optional.of(testCollection));
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorIdAndIdNot("Other Collection", userId, 1L))
                .thenReturn(true);

            BadRequestException ex = assertThrows(BadRequestException.class,
                () -> collectionService.updateCollection(1L, request));
            assertEquals("Ya tienes una colección con ese nombre", ex.getMessage());
        }
    }

    // Verifica que updateCollection con nombre null solo actualiza la descripción.
    @Test
    void updateCollection_NullName_OnlyUpdatesDescription() {
        Long userId = 1L;
        UpdateCollectionRequest request = new UpdateCollectionRequest();
        request.setName(null);
        request.setDescription("Only new description");

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(1L, userId)).thenReturn(Optional.of(testCollection));
            when(collectionRepository.save(testCollection)).thenReturn(testCollection);

            ApiResponse result = collectionService.updateCollection(1L, request);

            assertNotNull(result);
            assertEquals("Colección actualizada con éxito", result.getMessage());
            verify(collectionRepository, org.mockito.Mockito.never()).existsByNameIgnoreCaseAndAuthorIdAndIdNot(any(), any(), any());
        }
    }

    // Verifica que updateCollection con descripción null no sobreescribe la descripción existente.
    @Test
    void updateCollection_NullDescription_OnlyUpdatesName() {
        Long userId = 1L;
        UpdateCollectionRequest request = new UpdateCollectionRequest();
        request.setName("New Name");
        request.setDescription(null);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(1L, userId)).thenReturn(Optional.of(testCollection));
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorIdAndIdNot("New Name", userId, 1L))
                .thenReturn(false);
            when(collectionRepository.save(testCollection)).thenReturn(testCollection);

            ApiResponse result = collectionService.updateCollection(1L, request);

            assertNotNull(result);
            assertEquals("Colección actualizada con éxito", result.getMessage());
        }
    }

    // Verifica que updateCollection con el mismo nombre (ignorando mayúsculas) no actualiza el nombre.
    @Test
    void updateCollection_SameNameIgnoreCase_SkipsDuplicateCheckAndDoesNotChangeName() {
        Long userId = 1L;
        UpdateCollectionRequest request = new UpdateCollectionRequest();
        request.setName("TEST COLLECTION"); // mismo nombre en mayúsculas
        request.setDescription("Updated desc");

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(1L, userId)).thenReturn(Optional.of(testCollection));
            when(collectionRepository.save(testCollection)).thenReturn(testCollection);

            ApiResponse result = collectionService.updateCollection(1L, request);

            assertNotNull(result);
            verify(collectionRepository, org.mockito.Mockito.never()).existsByNameIgnoreCaseAndAuthorIdAndIdNot(any(), any(), any());
        }
    }

    // Verifica que getCollectionPosts lanza ResourceNotFoundException cuando la colección no existe.
    @Test
    void getCollectionPosts_CollectionNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(99L, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> collectionService.getCollectionPosts(99L));
        }
    }

    // Verifica que getCollectionPosts mapea correctamente un post con verificador externo.
    @Test
    void getCollectionPosts_PostWithExternalVerifier_MapsVerifiedBy() {
        Long userId = 1L;
        User verifierUser = new User();
        verifierUser.setId(2L);
        verifierUser.setProfileUsername("verifier");
        verifierUser.setRole(UserRole.INSTITUTION);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(1L, userId)).thenReturn(Optional.of(testCollection));
            when(collectionPostRepository.findByCollectionIdOrderByAddedAtDesc(1L))
                .thenReturn(List.of(testCollectionPost));
            when(verificationQueryService.isPostVerified(testPost)).thenReturn(true);
            when(verificationQueryService.getExternalVerifier(testPost.getId())).thenReturn(verifierUser);

            List<com.mypresentpast.backend.dto.response.PostResponse> result =
                collectionService.getCollectionPosts(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertNotNull(result.get(0).getVerifiedBy());
            assertEquals(2L, result.get(0).getVerifiedBy().getId());
        }
    }

    // Verifica que getCollectionPosts mapea correctamente un post con ubicación.
    @Test
    void getCollectionPosts_PostWithLocation_MapsLocation() {
        Long userId = 1L;
        Location location = new Location();
        location.setId(10L);
        location.setAddress("Test Street 123");
        location.setLatitude(-34.6037);
        location.setLongitude(-58.3816);
        testPost.setLocation(location);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(1L, userId)).thenReturn(Optional.of(testCollection));
            when(collectionPostRepository.findByCollectionIdOrderByAddedAtDesc(1L))
                .thenReturn(List.of(testCollectionPost));
            when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
            when(verificationQueryService.getExternalVerifier(testPost.getId())).thenReturn(null);

            List<com.mypresentpast.backend.dto.response.PostResponse> result =
                collectionService.getCollectionPosts(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertNotNull(result.get(0).getLocation());
            assertEquals("Test Street 123", result.get(0).getLocation().getAddress());
        }
    }

    // Verifica que getCollectionPosts mapea correctamente un post con media adjunta.
    @Test
    void getCollectionPosts_PostWithMedia_MapsMedia() {
        Long userId = 1L;
        Media media = new Media();
        media.setId(5L);
        media.setType(MediaType.IMAGE);
        media.setUrl("https://cloudinary.com/img.jpg");
        testPost.setMedia(List.of(media));

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(1L, userId)).thenReturn(Optional.of(testCollection));
            when(collectionPostRepository.findByCollectionIdOrderByAddedAtDesc(1L))
                .thenReturn(List.of(testCollectionPost));
            when(verificationQueryService.isPostVerified(testPost)).thenReturn(false);
            when(verificationQueryService.getExternalVerifier(testPost.getId())).thenReturn(null);

            List<com.mypresentpast.backend.dto.response.PostResponse> result =
                collectionService.getCollectionPosts(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertNotNull(result.get(0).getMedia());
            assertEquals(1, result.get(0).getMedia().size());
            assertEquals("https://cloudinary.com/img.jpg", result.get(0).getMedia().get(0).getUrl());
        }
    }

    // Verifica que getPostCollectionStatus lanza ResourceNotFoundException cuando el post no existe.
    @Test
    void getPostCollectionStatus_PostNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(postRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> collectionService.getPostCollectionStatus(99L));
        }
    }

    // Verifica que getPostCollectionStatus lanza BadRequestException cuando el post no está activo.
    @Test
    void getPostCollectionStatus_PostNotActive_ThrowsBadRequestException() {
        Long userId = 1L;
        testPost.setStatus(PostStatus.DELETED);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

            assertThrows(BadRequestException.class,
                () -> collectionService.getPostCollectionStatus(1L));
        }
    }

    // Verifica que addPostToCollection lanza ResourceNotFoundException cuando la colección no existe.
    @Test
    void addPostToCollection_CollectionNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(99L, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> collectionService.addPostToCollection(99L, 1L));
        }
    }

    // Verifica que addPostToCollection lanza ResourceNotFoundException cuando el post no existe.
    @Test
    void addPostToCollection_PostNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(1L, userId)).thenReturn(Optional.of(testCollection));
            when(postRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> collectionService.addPostToCollection(1L, 99L));
        }
    }

    // Verifica que addPostToCollection lanza BadRequestException cuando el post no está activo.
    @Test
    void addPostToCollection_PostNotActive_ThrowsBadRequestException() {
        Long userId = 1L;
        testPost.setStatus(PostStatus.DISABLED);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(1L, userId)).thenReturn(Optional.of(testCollection));
            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

            assertThrows(BadRequestException.class,
                () -> collectionService.addPostToCollection(1L, 1L));
        }
    }

    // Verifica que removePostFromCollection lanza ResourceNotFoundException cuando la colección no existe.
    @Test
    void removePostFromCollection_CollectionNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(99L, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> collectionService.removePostFromCollection(99L, 1L));
        }
    }

    // Verifica que removePostFromCollection lanza ResourceNotFoundException cuando el post no está en la colección.
    @Test
    void removePostFromCollection_PostNotInCollection_ThrowsResourceNotFoundException() {
        Long userId = 1L;

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.findByIdAndAuthorId(1L, userId)).thenReturn(Optional.of(testCollection));
            when(collectionPostRepository.findByCollectionIdAndPostId(1L, 99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> collectionService.removePostFromCollection(1L, 99L));
        }
    }

    // Verifica que createCollection lanza ResourceNotFoundException cuando el usuario no existe en la base.
    @Test
    void createCollection_UserNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("New Collection");

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.countByAuthorId(userId)).thenReturn(0L);
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorId("New Collection", userId)).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> collectionService.createCollection(request));
        }
    }

    // Verifica que createCollectionAndSavePost lanza BadRequestException cuando se alcanza el límite de colecciones.
    @Test
    void createCollectionAndSavePost_MaxCollectionsReached_ThrowsBadRequestException() {
        Long userId = 1L;
        CreateCollectionAndSavePostRequest request = new CreateCollectionAndSavePostRequest();
        request.setName("New");
        request.setPostId(1L);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.countByAuthorId(userId)).thenReturn(20L);

            assertThrows(BadRequestException.class,
                () -> collectionService.createCollectionAndSavePost(request));
        }
    }

    // Verifica que createCollectionAndSavePost lanza BadRequestException cuando el nombre ya existe.
    @Test
    void createCollectionAndSavePost_DuplicateName_ThrowsBadRequestException() {
        Long userId = 1L;
        CreateCollectionAndSavePostRequest request = new CreateCollectionAndSavePostRequest();
        request.setName("Existing");
        request.setPostId(1L);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.countByAuthorId(userId)).thenReturn(0L);
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorId("Existing", userId)).thenReturn(true);

            assertThrows(BadRequestException.class,
                () -> collectionService.createCollectionAndSavePost(request));
        }
    }

    // Verifica que createCollectionAndSavePost lanza ResourceNotFoundException cuando el post no existe.
    @Test
    void createCollectionAndSavePost_PostNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;
        CreateCollectionAndSavePostRequest request = new CreateCollectionAndSavePostRequest();
        request.setName("New");
        request.setPostId(99L);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.countByAuthorId(userId)).thenReturn(0L);
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorId("New", userId)).thenReturn(false);
            when(postRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> collectionService.createCollectionAndSavePost(request));
        }
    }

    // Verifica que createCollectionAndSavePost lanza BadRequestException cuando el post no está activo.
    @Test
    void createCollectionAndSavePost_PostNotActive_ThrowsBadRequestException() {
        Long userId = 1L;
        testPost.setStatus(PostStatus.DELETED);
        CreateCollectionAndSavePostRequest request = new CreateCollectionAndSavePostRequest();
        request.setName("New");
        request.setPostId(1L);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.countByAuthorId(userId)).thenReturn(0L);
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorId("New", userId)).thenReturn(false);
            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

            assertThrows(BadRequestException.class,
                () -> collectionService.createCollectionAndSavePost(request));
        }
    }

    // Verifica que createCollectionAndSavePost lanza ResourceNotFoundException cuando el usuario no existe.
    @Test
    void createCollectionAndSavePost_UserNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;
        CreateCollectionAndSavePostRequest request = new CreateCollectionAndSavePostRequest();
        request.setName("New");
        request.setPostId(1L);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> mockedSecurity =
             Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {

            mockedSecurity.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId)
                         .thenReturn(userId);
            when(collectionRepository.countByAuthorId(userId)).thenReturn(0L);
            when(collectionRepository.existsByNameIgnoreCaseAndAuthorId("New", userId)).thenReturn(false);
            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> collectionService.createCollectionAndSavePost(request));
        }
    }
}
