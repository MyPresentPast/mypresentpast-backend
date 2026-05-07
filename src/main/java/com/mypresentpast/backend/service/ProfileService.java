package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.request.ProfileUpdateRequest;
import com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest;
import com.mypresentpast.backend.dto.request.profile.EmailChangeRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.ProfileResponse;
import com.mypresentpast.backend.dto.response.ProfileUpdateResponse;
import jakarta.mail.MessagingException;
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
     * Inicia el flujo de cambio de email: valida contraseña, verifica unicidad del nuevo email
     * y envía un correo de verificación al nuevo email.
     *
     * @param request contiene el nuevo email y la contraseña de confirmación
     * @return respuesta con mensaje de confirmación del envío
     * @throws MessagingException si ocurre un error al enviar el correo
     */
    ApiResponse initiateEmailChange(EmailChangeRequest request) throws MessagingException;

    /**
     * Cancela un cambio de email pendiente eliminando el token asociado.
     */
    void cancelEmailChange();

    /**
     * Reenvía el correo de verificación para un cambio de email pendiente.
     *
     * @return respuesta con mensaje de confirmación del reenvío
     * @throws MessagingException si ocurre un error al enviar el correo
     */
    ApiResponse resendEmailChange() throws MessagingException;

    /**
     * Sube una imagen de avatar del usuario a Cloudinary.
     * La imagen se almacena en una estructura organizada por ID de usuario.
     *
     * @param file archivo de imagen que representa el nuevo avatar
     * @return URL pública del avatar subido
     */
    String uploadAvatar(MultipartFile file);
}
