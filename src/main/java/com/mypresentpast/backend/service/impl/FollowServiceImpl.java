package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.FollowStatsResponse;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Follow;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.FollowRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.FollowService;
import com.mypresentpast.backend.utils.MessageBundle;
import com.mypresentpast.backend.utils.SecurityUtils;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de Follow.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse follow(Long followeeId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // Validar que no se pueda seguir a sí mismo
        if (currentUserId.equals(followeeId)) {
            throw new IllegalArgumentException("No puedes seguirte a ti mismo");
        }

        // Verificar que el usuario a seguir existe
        User followee = userRepository.findById(followeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + followeeId));

        // Verificar que no ya esté siguiendo al usuario
        if (followRepository.existsByFollowerIdAndFolloweeId(currentUserId, followeeId)) {
            throw new IllegalArgumentException("Ya sigues a este usuario");
        }

        // Obtener el usuario actual
        User follower = userRepository.findById(currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario actual no encontrado"));

        // Crear la relación de seguimiento
        Follow follow = Follow.builder()
            .follower(follower)
            .followee(followee)
            .build();

        followRepository.save(follow);
        
        log.info("Usuario {} comenzó a seguir al usuario {}", currentUserId, followeeId);

        return ApiResponse.builder()
            .message(String.format("Ahora sigues a %s", followee.getProfileUsername()))
            .build();
    }

    @Override
    public ApiResponse unfollow(Long followeeId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // Verificar que el usuario a dejar de seguir existe
        User followee = userRepository.findById(followeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + followeeId));

        // Verificar que actualmente sigue al usuario
        Follow follow = followRepository.findByFollowerIdAndFolloweeId(currentUserId, followeeId)
            .orElseThrow(() -> new IllegalArgumentException("No sigues a este usuario"));

        // Eliminar la relación de seguimiento
        followRepository.delete(follow);
        
        log.info("Usuario {} dejó de seguir al usuario {}", currentUserId, followeeId);

        return ApiResponse.builder()
            .message(String.format("Ya no sigues a %s", followee.getProfileUsername()))
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getMyFollowing() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        List<User> following = followRepository.findFollowingByUserId(currentUserId);
        
        return following.stream()
            .map(this::mapToUserDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getMyFollowers() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        List<User> followers = followRepository.findFollowersByUserId(currentUserId);
        
        return followers.stream()
            .map(this::mapToUserDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getFollowingByUserId(Long userId) {
        List<User> following = followRepository.findFollowingByUserId(userId);
        return following.stream()
                .map(this::mapToUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getFollowersByUserId(Long userId) {
        List<User> followers = followRepository.findFollowersByUserId(userId);
        return followers.stream()
                .map(this::mapToUserDto)
                .toList();
    }



    @Override
    @Transactional(readOnly = true)
    public Boolean isFollowing(Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return followRepository.existsByFollowerIdAndFolloweeId(currentUserId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public FollowStatsResponse getFollowStats(Long userId) {
        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isFollowing = followRepository.existsByFollowerIdAndFolloweeId(currentUserId, userId);
        
        long followersCount = followRepository.countByFolloweeId(userId);
        long followingCount = followRepository.countByFollowerId(userId);

        return FollowStatsResponse.builder()
            .userId(userId)
            .profileUsername(user.getProfileUsername())
            .followersCount(followersCount)
            .followingCount(followingCount)
            .isFollowing(isFollowing)
            .build();
    }

    /**
     * Convierte un User a UserDto.
     */
    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .name(user.getProfileUsername())
            .type(user.getRole())
            .avatar(user.getAvatar())
            .build();
    }


}
