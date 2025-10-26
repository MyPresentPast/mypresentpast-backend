package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.VerificationToken;
import jakarta.mail.MessagingException;

/**
 * Servicio responsable de la gestión de tokens de verificación de correo electrónico
 * y del proceso de confirmación de cuenta de usuario.
 */
public interface VerificationService {
    /**
     * Crea un nuevo token de verificación para el usuario especificado.
     * Si existía uno anterior, lo elimina.
     *
     * @param user Usuario para quien se generará el token.
     * @return El token de verificación creado y persistido.
     */
    VerificationToken createVerificationToken(User user);

    /**
     * Valida un token de verificación.
     * Si es válido, marca el email del usuario como verificado y elimina el token.
     *
     * @param token El token de verificación recibido.
     * @return Una respuesta con un mensaje de confirmación.
     * @throws com.mypresentpast.backend.exception.ResourceNotFoundException Si el token no existe.
     * @throws com.mypresentpast.backend.exception.BadRequestException Si el token ha expirado.
     */
    ApiResponse validateVerificationToken(String token);

    /**
     * Reenvía un correo de verificación a un usuario cuyo email no ha sido confirmado aún.
     * Solo funciona si el token existente aún no ha expirado.
     *
     * @param email El correo del usuario al que se le reenviará el mensaje.
     * @throws com.mypresentpast.backend.exception.ResourceNotFoundException Si el usuario o el token no existen.
     * @throws com.mypresentpast.backend.exception.BadRequestException Si el token ha expirado o el email ya está verificado.
     * @throws MessagingException Si ocurre un error al enviar el correo.
     */
    void resendVerification(String email) throws MessagingException;
}
