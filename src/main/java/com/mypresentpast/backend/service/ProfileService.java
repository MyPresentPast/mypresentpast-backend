package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.request.ProfileUpdateRequest;
import com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest;
import com.mypresentpast.backend.dto.response.ProfileResponse;
import com.mypresentpast.backend.dto.response.ProfileUpdateResponse;
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

    /**
     * Actualiza los datos del perfil del usuario.
     * Puede incluir campos como profileUsername, name, lastname y email.
     *
     * @param request objeto con los nuevos datos del perfil
     * @return información del perfil actualizada
     */
    ProfileUpdateResponse updateProfile(ProfileUpdateRequest request);

    /**
     * Cambia la contraseña del usuario.
     * Requiere la contraseña actual y la nueva para validar el cambio.
     *
     * @param userId ID del usuario que realiza el cambio
     * @param request contiene la contraseña actual y la nueva
     */
    void changePassword(Long userId, ChangePasswordRequest request);

    /**
     * Sube una imagen de avatar del usuario a Cloudinary.
     * La imagen se almacena en una estructura organizada por ID de usuario.
     *
     * @param file archivo de imagen que representa el nuevo avatar
     * @return URL pública del avatar subido
     */
    String uploadAvatar(MultipartFile file);
}
