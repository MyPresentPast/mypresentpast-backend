package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.ProfileUpdateRequest;
import com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest;
import com.mypresentpast.backend.dto.response.ProfileResponse;
import com.mypresentpast.backend.dto.response.ProfileUpdateResponse;
import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.exception.UnauthorizedException;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.FollowRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.CloudinaryService;
import com.mypresentpast.backend.service.ProfileService;
import com.mypresentpast.backend.utils.CommonFunctions;
import com.mypresentpast.backend.utils.MessageBundle;
import com.mypresentpast.backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

/**
 * Implementación del servicio de Profile.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

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
            .email(isSelf ? user.getEmail() : null) // Solo incluir email si es perfil propio
            .avatarUrl(user.getAvatar())
            .userType(user.getRole())
            .isSelf(isSelf)
            .following(isFollowing)
            .postCount(postCount)
            .followerCount(followerCount)
            .followingCount(followingCount)
            .build();
    }

    @Override
    public ProfileUpdateResponse updateProfile(ProfileUpdateRequest request) {
        // Obtener el ID del usuario autenticado
        Long userId = SecurityUtils.getCurrentUserId();

        // Cargar la entidad o fallar rápido si no existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(MessageBundle.USER_NOT_FOUND_WITH_ID, userId)));

        // Delegar por campo para reducir complejidad y aislar validaciones
        applyEmailUpdate(user, request.getEmail());
        applyUsernameUpdate(user, request.getProfileUsername());
        applyNameUpdate(user, request.getName());
        applyLastNameUpdate(user, request.getLastName());

        // Persistir cambios
        User saved = userRepository.save(user);

        // Mapear a DTO de salida
        return new ProfileUpdateResponse(
                saved.getId(),
                saved.getProfileUsername(),
                saved.getEmail(),
                saved.getName(),
                saved.getLastName()
        );

    }

    /* Normaliza y actualiza el email si fue enviado */
    private void applyEmailUpdate(User user, String email) {
        if (email == null) return; // ignorar campos no enviados (PATCH semantics)

        String newEmail = CommonFunctions.safeTrim(email);
        if (newEmail != null) newEmail = newEmail.toLowerCase(); // emails en minúsculas

        // No-op si no hay cambios (evita hits innecesarios a BD)
        if (Objects.equals(newEmail, user.getEmail())) return;

        // Validar unicidad solo si cambia (reduce consultas)
        if (userRepository.existsByEmail(newEmail)) {
            throw new DataIntegrityViolationException(String.format(
                    MessageBundle.DUPLICATE_EMAIL, newEmail));
        }

        user.setEmail(newEmail);
    }

    /* Normaliza y actualiza el username si fue enviado */
    private void applyUsernameUpdate(User user, String profileUsername) {
        if (profileUsername == null) return;

        String newUsername = CommonFunctions.safeTrim(profileUsername);

        if (Objects.equals(newUsername, user.getProfileUsername())) return;

        if (userRepository.existsByProfileUsername(newUsername)) {
            throw new DataIntegrityViolationException(String.format(
                    MessageBundle.DUPLICATE_USERNAME, newUsername));
        }

        user.setProfileUsername(newUsername);
    }

    /* Aplica Title Case a name y actualiza si fue enviado */
    private void applyNameUpdate(User user, String name) {
        if (name == null) return;

        String newName = CommonFunctions.formatAsTitleCase(name);

        if (Objects.equals(newName, user.getName())) return;

        user.setName(newName);
    }

    /* Aplica Title Case a lastName y actualiza si fue enviado */
    private void applyLastNameUpdate(User user, String lastName) {
        if (lastName == null) return;

        String newLastName = CommonFunctions.formatAsTitleCase(lastName);

        if (Objects.equals(newLastName, user.getLastName())) return;

        user.setLastName(newLastName);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        // Validaciones funcionales
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(MessageBundle.PASSWORD_MISMATCH);
        }
        if (!CommonFunctions.isValidPassword(request.getNewPassword())) {
            throw new BadRequestException(MessageBundle.PASSWORD_INVALID);
        }

        // Busqueda Usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(MessageBundle.USER_NOT_FOUND_WITH_ID, userId)));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException(MessageBundle.CURRENT_PASSWORD_INVALID);
        }

        // Evitar misma contraseña
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException(MessageBundle.NEW_PASSWORD_SAME_AS_OLD);
        }

        // Persistir
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(MessageBundle.USER_NOT_FOUND_WITH_ID, userId)));

        Map<String, Object> result = cloudinaryService.uploadAvatar(file, userId);

        String secureUrl = (String) result.getOrDefault("secure_url", result.get("url"));

        user.setAvatar(secureUrl);
        userRepository.save(user);

        return secureUrl;
    }

}
