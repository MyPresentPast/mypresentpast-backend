package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.ProfileResponse;
import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.exception.UnauthorizedException;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.FollowRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.ProfileService;
import com.mypresentpast.backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de Profile.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        // Validación de entrada
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        
        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        // Obtener el ID del usuario actual (si está autenticado)
        Long currentUserId = null;
        try {
            currentUserId = SecurityUtils.getCurrentUserId();
        } catch (UnauthorizedException e) {
            // Usuario no autenticado, currentUserId queda null
            log.debug("Usuario no autenticado accediendo al perfil {}", userId);
        }

        // Determinar si es el perfil propio
        boolean isSelf = currentUserId != null && currentUserId.equals(userId);

        // Verificar si el usuario actual sigue a este usuario
        boolean isFollowing = false;
        if (currentUserId != null && !isSelf) {
            isFollowing = followRepository.existsByFollowerIdAndFolloweeId(currentUserId, userId);
        }

        // Obtener estadísticas
        long followerCount = followRepository.countByFolloweeId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        long postCount = postRepository.countByAuthorIdAndStatus(userId, PostStatus.ACTIVE);

        // Construir respuesta
        return ProfileResponse.builder()
            .id(user.getId())
            .profileUsername(user.getProfileUsername())
            .name(user.getName())
            .lastName(user.getLastName())
            .avatarUrl(user.getAvatar())
            .userType(user.getRole())
            .isSelf(isSelf)
            .following(isFollowing)
            .postCount(postCount)
            .followerCount(followerCount)
            .followingCount(followingCount)
            .build();
    }
}
