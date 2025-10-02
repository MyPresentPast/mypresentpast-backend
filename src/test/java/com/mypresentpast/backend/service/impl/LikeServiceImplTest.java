package com.mypresentpast.backend.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

import com.mypresentpast.backend.dto.response.LikeStatusResponse;
import com.mypresentpast.backend.dto.response.LikeToggleResponse;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.PostLike;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.PostLikeRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.utils.SecurityUtils;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User testUser;
    private Post testPost;
    private PostLike testPostLike;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setProfileUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole(UserRole.NORMAL);

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");

        testPostLike = new PostLike();
        testPostLike.setId(1L);
        testPostLike.setUser(testUser);
        testPostLike.setPost(testPost);
    }

    @Test
    void toggleLike_AddLike_Success() {
        // Given
        Long postId = 1L;
        Long userId = 1L;

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(postLikeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Optional.empty());
            when(postLikeRepository.save(any(PostLike.class))).thenReturn(testPostLike);
            when(postLikeRepository.countByPostId(postId)).thenReturn(1L);

            // When
            LikeToggleResponse response = likeService.toggleLike(postId);

            // Then
            assertNotNull(response);
            assertEquals("Like agregado con éxito", response.getMessage());
            assertTrue(response.getIsLiked());
            assertEquals(1L, response.getTotalLikes());

            verify(postLikeRepository).save(any(PostLike.class));
        }
    }

    @Test
    void toggleLike_RemoveLike_Success() {
        // Given
        Long postId = 1L;
        Long userId = 1L;

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(postLikeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Optional.of(testPostLike));
            when(postLikeRepository.countByPostId(postId)).thenReturn(0L);

            // When
            LikeToggleResponse response = likeService.toggleLike(postId);

            // Then
            assertNotNull(response);
            assertEquals("Like removido con éxito", response.getMessage());
            assertFalse(response.getIsLiked());
            assertEquals(0L, response.getTotalLikes());

            verify(postLikeRepository).delete(testPostLike);
        }
    }

    @Test
    void toggleLike_PostNotFound_ThrowsException() {
        // Given
        Long postId = 999L;
        Long userId = 1L;

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> likeService.toggleLike(postId)
            );

            assertEquals("Post no encontrado con id: 999", exception.getMessage());
        }
    }

    @Test
    void getLikeStatus_Success() {
        // Given
        Long postId = 1L;
        Long userId = 1L;

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            when(postRepository.existsById(postId)).thenReturn(true);
            when(postLikeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(true);
            when(postLikeRepository.countByPostId(postId)).thenReturn(5L);

            // When
            LikeStatusResponse response = likeService.getLikeStatus(postId);

            // Then
            assertNotNull(response);
            assertTrue(response.getIsLiked());
            assertEquals(5L, response.getTotalLikes());
        }
    }

    @Test
    void getLikeStatus_PostNotFound_ThrowsException() {
        // Given
        Long postId = 999L;

        when(postRepository.existsById(postId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> likeService.getLikeStatus(postId)
        );

        assertEquals("Post no encontrado con id: 999", exception.getMessage());
    }

    @Test
    void getTotalLikes_Success() {
        // Given
        Long postId = 1L;
        when(postLikeRepository.countByPostId(postId)).thenReturn(10L);

        // When
        Long totalLikes = likeService.getTotalLikes(postId);

        // Then
        assertEquals(10L, totalLikes);
    }

    @Test
    void isLikedByCurrentUser_UserAuthenticated_ReturnsTrue() {
        // Given
        Long postId = 1L;
        Long userId = 1L;

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
            when(postLikeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(true);

            // When
            Boolean isLiked = likeService.isLikedByCurrentUser(postId);

            // Then
            assertTrue(isLiked);
        }
    }

    @Test
    void isLikedByCurrentUser_UserNotAuthenticated_ReturnsFalse() {
        // Given
        Long postId = 1L;

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenThrow(new RuntimeException("Not authenticated"));

            // When
            Boolean isLiked = likeService.isLikedByCurrentUser(postId);

            // Then
            assertFalse(isLiked);
        }
    }
}
