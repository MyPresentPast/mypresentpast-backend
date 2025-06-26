package com.mypresentpast.backend.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.request.CreatePostRequest;
import com.mypresentpast.backend.dto.response.MapResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import com.mypresentpast.backend.dto.request.UpdatePostRequest;
import com.mypresentpast.backend.enums.Category;
import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.enums.UserRol;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Location;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.LocationRepository;
import com.mypresentpast.backend.repository.MediaRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.CloudinaryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile mockImage;

    @InjectMocks
    private PostServiceImpl postService;

    private User testUser;
    private Location testLocation;
    private Post testPost;
    private CreatePostRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .name("Test User")
            .password("password")
            .type(UserRol.NORMAL)
            .build();

        testLocation = Location.builder()
            .id(1L)
            .address("Test Address")
            .latitude(-34.6118)
            .longitude(-58.3960)
            .build();

        testPost = Post.builder()
            .id(1L)
            .title("Test Post")
            .content("Test Content")
            .date(LocalDate.now())
            .postedAt(LocalDate.now())
            .category(Category.STORY)
            .isByIA(false)
            .isVerified(true)
            .status(PostStatus.ACTIVE)
            .author(testUser)
            .location(testLocation)
            .media(new ArrayList<>())
            .build();

        createRequest = CreatePostRequest.builder()
            .title("New Test Post")
            .content("New test content")
            .date(LocalDate.now())
            .category(Category.STORY)
            .isByIA(false)
            .authorId(1L)
            .latitude(-34.6118)
            .longitude(-58.3960)
            .address("Buenos Aires, Argentina")
            .build();
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
        when(cloudinaryService.upload(mockImage)).thenReturn(uploadResult);
        when(mediaRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

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
        verify(postRepository, never()).save(any());
    }

    @Test
    void createPost_TooManyImages_ThrowsException() {
        // Given
        List<MultipartFile> tooManyImages = Arrays.asList(
            mockImage, mockImage, mockImage, mockImage, mockImage, mockImage // 6 images
        );
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
    void createPost_ReuseExistingLocation() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.findLocationsByProximity(anyDouble(), anyDouble()))
            .thenReturn(Arrays.asList(testLocation)); // Location already exists
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        ApiResponse response = postService.createPost(createRequest, null);

        // Then
        assertNotNull(response);
        verify(locationRepository).findLocationsByProximity(-34.6118, -58.3960);
        verify(locationRepository, never()).save(any()); // Should not save new location
    }

    @Test
    void getPostById_Success() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // When
        PostResponse response = postService.getPostById(1L);

        // Then
        assertNotNull(response);
        assertEquals("Test Post", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals(testUser.getName(), response.getAuthor().getName());
        verify(postRepository).findById(1L);
    }

    @Test
    void getPostById_NotFound_ThrowsException() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> postService.getPostById(1L)
        );

        assertEquals("Publicación no encontrada con id: 1", exception.getMessage());
    }

    @Test
    void getMapData_Success_WithFilters() {
        // Given
        List<Post> mockPosts = Arrays.asList(testPost);
        when(postRepository.findPostsInAreaWithFilters(
            anyDouble(), anyDouble(), anyDouble(), anyDouble(),
            anyString(), any(LocalDate.class)))
            .thenReturn(mockPosts);

        // When
        MapResponse response = postService.getMapData(
            -35.0, -34.0, -59.0, -58.0, "STORY", LocalDate.now()
        );

        // Then
        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        assertEquals("Test Post", response.getPosts().get(0).getTitle());
        verify(postRepository).findPostsInAreaWithFilters(
            -35.0, -34.0, -59.0, -58.0, "STORY", LocalDate.now()
        );
    }

    @Test
    void getMapData_InvalidCategory_HandledGracefully() {
        // Given
        List<Post> mockPosts = Arrays.asList(testPost);
        when(postRepository.findPostsInAreaWithFilters(
            anyDouble(), anyDouble(), anyDouble(), anyDouble(),
            eq(""), any()))
            .thenReturn(mockPosts);

        // When
        MapResponse response = postService.getMapData(
            -35.0, -34.0, -59.0, -58.0, "INVALID_CATEGORY", null
        );

        // Then
        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        verify(postRepository).findPostsInAreaWithFilters(
            -35.0, -34.0, -59.0, -58.0, "", null
        );
    }

    @Test
    void getRandomPost_Success() {
        // Given
        List<Post> activePosts = Arrays.asList(testPost);
        when(postRepository.findByStatusAndLocationIsNotNull(PostStatus.ACTIVE))
            .thenReturn(activePosts);

        // When
        PostResponse response = postService.getRandomPost();

        // Then
        assertNotNull(response);
        assertEquals("Test Post", response.getTitle());
        verify(postRepository).findByStatusAndLocationIsNotNull(PostStatus.ACTIVE);
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
        UpdatePostRequest updateRequest = UpdatePostRequest.builder()
            .title("Updated Title")
            .content("Updated Content")
            .category(Category.INFORMATION)
            .authorId(1L) // Necesario para el update
            .latitude(-34.6118)
            .longitude(-58.3960)
            .address("Updated Address")
            .date(LocalDate.now())
            .build();

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
    void deletePost_Success() {
        // Given - deletePost hace eliminación LÓGICA, no física
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        ApiResponse response = postService.deletePost(1L);

        // Then
        assertNotNull(response);
        assertEquals("Publicación eliminada con éxito", response.getMessage());
        // No debe llamar a cloudinaryService.delete porque es eliminación lógica
        verify(cloudinaryService, never()).delete(anyString());
        verify(postRepository).save(testPost); // Guarda con status DELETED
        verify(postRepository, never()).delete(testPost); // No eliminación física

        // Verificar que el status cambió a DELETED
        assertEquals(PostStatus.DELETED, testPost.getStatus());
    }
} 