package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.request.EmailRequest;
import jakarta.mail.MessagingException;

/**
 * Servicio responsable del envío de correos electrónicos utilizando plantillas HTML.
 */
public interface EmailService {

    /**
     * Envía un correo electrónico utilizando los datos especificados en la solicitud.
     *
     * @param request Objeto que contiene la información del destinatario, asunto,
     *                nombre del usuario y URL de verificación.
     * @throws RuntimeException Si ocurre un error durante el envío del correo.
     */
    void sendMail(EmailRequest request) throws MessagingException;
}
