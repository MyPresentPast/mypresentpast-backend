package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest;
import com.mypresentpast.backend.dto.response.ProfileResponse;
import com.mypresentpast.backend.dto.request.ProfileUpdateRequest;
import com.mypresentpast.backend.dto.response.ProfileUpdateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio para manejar operaciones relacionadas con perfiles de usuario.
 */
public interface ProfileService {

    /**
     * Obtener perfil de usuario por ID.
     * Devuelve el perfil con toda la información necesaria para mostrar
     * tanto perfiles propios como ajenos.
     *
     * @param userId ID del usuario del perfil a obtener
     * @return información completa del perfil con estadísticas
     */
    ProfileResponse getProfile(Long userId);


    ProfileUpdateResponse updateProfile(ProfileUpdateRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    String uploadAvatar(MultipartFile file);
}
