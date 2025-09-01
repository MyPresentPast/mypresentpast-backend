package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.FollowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private FollowServiceImpl followService;

    @Test
    void getFollowingByUserId_validUserId_returnsMappedList() {
        Long userId = 42L;

        User user1 = new User();
        user1.setId(1L);
        user1.setProfileUsername("alice");

        User user2 = new User();
        user2.setId(2L);
        user2.setProfileUsername("bob");

        List<User> repoResult = List.of(user1, user2);
        when(followRepository.findFollowingByUserId(userId)).thenReturn(repoResult);

        List<UserDto> result = followService.getFollowingByUserId(userId);

        // Assertions con JUnit
        assertNotNull(result);
        assertEquals(2, result.size());

        UserDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("alice", dto1.getName());

        UserDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("bob", dto2.getName());

        // Verificación del repo
        verify(followRepository).findFollowingByUserId(userId);
    }


    @Test
    void getFollowingByUserId_userWithoutFollowing_returnsEmptyList() {
        Long userId = 99L;
        when(followRepository.findFollowingByUserId(userId)).thenReturn(List.of());

        List<UserDto> result = followService.getFollowingByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(followRepository).findFollowingByUserId(userId);
    }

    @Test
    void getFollowersByUserId_validUserId_returnsMappedList() {
        Long userId = 7L;

        User u1 = new User();
        u1.setId(10L);
        u1.setProfileUsername("carol");

        User u2 = new User();
        u2.setId(11L);
        u2.setProfileUsername("dave");

        when(followRepository.findFollowersByUserId(userId)).thenReturn(List.of(u1, u2));

        List<UserDto> result = followService.getFollowersByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        UserDto dto1 = result.get(0);
        assertEquals(10L, dto1.getId());
        assertEquals("carol", dto1.getName());

        UserDto dto2 = result.get(1);
        assertEquals(11L, dto2.getId());
        assertEquals("dave", dto2.getName());

        verify(followRepository).findFollowersByUserId(userId);
    }

    @Test
    void getFollowersByUserId_userWithoutFollowers_returnsEmptyList() {
        Long userId = 123L;
        when(followRepository.findFollowersByUserId(userId)).thenReturn(List.of());

        List<UserDto> result = followService.getFollowersByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(followRepository).findFollowersByUserId(userId);
    }

}