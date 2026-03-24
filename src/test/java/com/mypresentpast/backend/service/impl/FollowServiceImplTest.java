package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.FollowStatsResponse;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Follow;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.FollowRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

    // ── Mocks ──────────────────────────────────────────────────────────────
    @Mock
    private FollowRepository followRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowServiceImpl followService;

    // ── Datos de test reutilizables ────────────────────────────────────────
    private User testUser;
    private User followeeUser;

    // ── Setup ──────────────────────────────────────────────────────────────
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setProfileUsername("currentuser");
        testUser.setRole(UserRole.NORMAL);
        testUser.setAvatar("avatar1.png");

        followeeUser = new User();
        followeeUser.setId(2L);
        followeeUser.setProfileUsername("john_doe");
        followeeUser.setRole(UserRole.NORMAL);
        followeeUser.setAvatar("avatar2.png");
    }

    // ── follow ─────────────────────────────────────────────────────────────

    // Verifica que seguir a un usuario válido persiste la relación y retorna el mensaje correcto.
    @Test
    void follow_ValidRequest_SavesFollowAndReturnsMessage() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findById(2L)).thenReturn(Optional.of(followeeUser));
            when(followRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            Follow expectedFollow = Follow.builder()
                    .follower(testUser)
                    .followee(followeeUser)
                    .build();

            ApiResponse response = followService.follow(2L);

            assertNotNull(response);
            assertEquals("Ahora sigues a " + followeeUser.getProfileUsername(), response.getMessage());
            verify(followRepository).save(refEq(expectedFollow, "id"));
        }
    }

    // Verifica que un usuario no puede seguirse a sí mismo.
    @Test
    void follow_SelfFollow_ThrowsIllegalArgumentException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            assertThrows(IllegalArgumentException.class, () -> followService.follow(1L));

            verify(followRepository, never()).save(any());
        }
    }

    // Verifica que seguir a un usuario inexistente lanza ResourceNotFoundException.
    @Test
    void follow_FolloweeNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findById(2L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> followService.follow(2L));

            verify(followRepository, never()).save(any());
        }
    }

    // Verifica que no se puede seguir a un usuario que ya se sigue.
    @Test
    void follow_AlreadyFollowing_ThrowsIllegalArgumentException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findById(2L)).thenReturn(Optional.of(followeeUser));
            when(followRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> followService.follow(2L));

            verify(followRepository, never()).save(any());
        }
    }

    // Verifica que si el usuario actual no existe en la DB se lanza ResourceNotFoundException.
    @Test
    void follow_CurrentUserNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findById(2L)).thenReturn(Optional.of(followeeUser));
            when(followRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(false);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> followService.follow(2L));

            verify(followRepository, never()).save(any());
        }
    }

    // ── unfollow ───────────────────────────────────────────────────────────

    // Verifica que dejar de seguir a un usuario elimina la relación y retorna el mensaje correcto.
    @Test
    void unfollow_ValidRequest_DeletesFollowAndReturnsMessage() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            Follow existingFollow = Follow.builder().id(10L).follower(testUser).followee(followeeUser).build();
            when(userRepository.findById(2L)).thenReturn(Optional.of(followeeUser));
            when(followRepository.findByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(Optional.of(existingFollow));

            ApiResponse response = followService.unfollow(2L);

            assertNotNull(response);
            assertEquals("Ya no sigues a " + followeeUser.getProfileUsername(), response.getMessage());
            verify(followRepository).delete(existingFollow);
        }
    }

    // Verifica que dejar de seguir a un usuario inexistente lanza ResourceNotFoundException.
    @Test
    void unfollow_FolloweeNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findById(2L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> followService.unfollow(2L));

            verify(followRepository, never()).delete(any());
        }
    }

    // Verifica que no se puede dejar de seguir a alguien que no se sigue.
    @Test
    void unfollow_NotFollowing_ThrowsIllegalArgumentException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findById(2L)).thenReturn(Optional.of(followeeUser));
            when(followRepository.findByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> followService.unfollow(2L));

            verify(followRepository, never()).delete(any());
        }
    }

    // ── getMyFollowing ─────────────────────────────────────────────────────

    // Verifica que se retornan los usuarios seguidos del usuario actual correctamente mapeados a DTOs.
    @Test
    void getMyFollowing_WithFollowing_ReturnsMappedDtoList() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(followRepository.findFollowingByUserId(1L)).thenReturn(List.of(followeeUser));

            List<UserDto> result = followService.getMyFollowing();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(2L, result.get(0).getId());
            assertEquals("john_doe", result.get(0).getName());
            verify(followRepository).findFollowingByUserId(1L);
        }
    }

    // Verifica que si el usuario no sigue a nadie se retorna una lista vacía.
    @Test
    void getMyFollowing_NoFollowing_ReturnsEmptyList() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(followRepository.findFollowingByUserId(1L)).thenReturn(List.of());

            List<UserDto> result = followService.getMyFollowing();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ── getMyFollowers ─────────────────────────────────────────────────────

    // Verifica que se retornan los seguidores del usuario actual correctamente mapeados a DTOs.
    @Test
    void getMyFollowers_WithFollowers_ReturnsMappedDtoList() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(followRepository.findFollowersByUserId(1L)).thenReturn(List.of(followeeUser));

            List<UserDto> result = followService.getMyFollowers();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(2L, result.get(0).getId());
            assertEquals("john_doe", result.get(0).getName());
            verify(followRepository).findFollowersByUserId(1L);
        }
    }

    // Verifica que si el usuario no tiene seguidores se retorna una lista vacía.
    @Test
    void getMyFollowers_NoFollowers_ReturnsEmptyList() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(followRepository.findFollowersByUserId(1L)).thenReturn(List.of());

            List<UserDto> result = followService.getMyFollowers();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ── isFollowing ────────────────────────────────────────────────────────

    // Verifica que retorna true cuando el usuario actual sigue al usuario consultado.
    @Test
    void isFollowing_WhenFollowing_ReturnsTrue() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(followRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(true);

            Boolean result = followService.isFollowing(2L);

            assertTrue(result);
            verify(followRepository).existsByFollowerIdAndFolloweeId(1L, 2L);
        }
    }

    // Verifica que retorna false cuando el usuario actual no sigue al usuario consultado.
    @Test
    void isFollowing_WhenNotFollowing_ReturnsFalse() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(followRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(false);

            Boolean result = followService.isFollowing(2L);

            assertFalse(result);
        }
    }

    // ── getFollowStats ─────────────────────────────────────────────────────

    // Verifica que se retornan las estadísticas de seguimiento correctas para un usuario existente.
    @Test
    void getFollowStats_ValidUser_ReturnsCorrectStats() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findById(2L)).thenReturn(Optional.of(followeeUser));
            when(followRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(true);
            when(followRepository.countByFolloweeId(2L)).thenReturn(5L);
            when(followRepository.countByFollowerId(2L)).thenReturn(3L);

            FollowStatsResponse response = followService.getFollowStats(2L);

            assertNotNull(response);
            assertEquals(2L, response.getUserId());
            assertEquals("john_doe", response.getProfileUsername());
            assertEquals(5L, response.getFollowersCount());
            assertEquals(3L, response.getFollowingCount());
            assertTrue(response.isFollowing());
        }
    }

    // Verifica que consultar estadísticas de un usuario inexistente lanza ResourceNotFoundException.
    @Test
    void getFollowStats_UserNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> followService.getFollowStats(99L));
        }
    }

    // ── getFollowingByUserId (tests existentes) ────────────────────────────

    // Verifica que los usuarios seguidos por un userId dado se retornan correctamente mapeados a DTOs.
    @Test
    void getFollowingByUserId_ValidUserId_ReturnsMappedDtoList() {
        when(followRepository.findFollowingByUserId(1L)).thenReturn(List.of(testUser, followeeUser));

        List<UserDto> result = followService.getFollowingByUserId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("currentuser", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("john_doe", result.get(1).getName());
        verify(followRepository).findFollowingByUserId(1L);
    }

    // Verifica que si el usuario no sigue a nadie se retorna una lista vacía.
    @Test
    void getFollowingByUserId_NoFollowing_ReturnsEmptyList() {
        when(followRepository.findFollowingByUserId(1L)).thenReturn(List.of());

        List<UserDto> result = followService.getFollowingByUserId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(followRepository).findFollowingByUserId(1L);
    }

    // Verifica que los seguidores de un userId dado se retornan correctamente mapeados a DTOs.
    @Test
    void getFollowersByUserId_ValidUserId_ReturnsMappedDtoList() {
        when(followRepository.findFollowersByUserId(1L)).thenReturn(List.of(testUser, followeeUser));

        List<UserDto> result = followService.getFollowersByUserId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("currentuser", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("john_doe", result.get(1).getName());
        verify(followRepository).findFollowersByUserId(1L);
    }

    // Verifica que si el usuario no tiene seguidores se retorna una lista vacía.
    @Test
    void getFollowersByUserId_NoFollowers_ReturnsEmptyList() {
        when(followRepository.findFollowersByUserId(1L)).thenReturn(List.of());

        List<UserDto> result = followService.getFollowersByUserId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(followRepository).findFollowersByUserId(1L);
    }
}
