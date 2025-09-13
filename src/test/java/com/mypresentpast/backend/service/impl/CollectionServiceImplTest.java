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
}
