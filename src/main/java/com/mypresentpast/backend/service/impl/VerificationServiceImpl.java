package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.EmailRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.VerificationToken;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.repository.VerificationTokenRepository;
import com.mypresentpast.backend.service.EmailService;
import com.mypresentpast.backend.service.VerificationService;
import com.mypresentpast.backend.utils.MessageBundle;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final long EXPIRATION_HOURS = 24;


    @Override
    public VerificationToken createVerificationToken(User user) {
        // si ya existía un token viejo, lo borramos
        tokenRepository.deleteByUser(user);

        // creamos un nuevo token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS));

        return tokenRepository.save(verificationToken);
    }

    @Override
    public ApiResponse validateVerificationToken(String token) {
        VerificationToken vToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(MessageBundle.TOKEN_NOT_FOUND));

        if (vToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(MessageBundle.TOKEN_EXPIRED);
        }

        User user = vToken.getUser();

        if (vToken.getPendingEmail() != null) {
            user.setEmail(vToken.getPendingEmail());
            userRepository.save(user);
            tokenRepository.delete(vToken);
            return ApiResponse.builder().message(MessageBundle.EMAIL_CHANGE_CONFIRMED).build();
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.delete(vToken);
        return ApiResponse.builder().message("Email confirmado con éxito").build();
    }

    @Override
    public void initiateEmailChange(User user, String newEmail) throws MessagingException {
        tokenRepository.deleteByUser(user);

        String tokenValue = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(tokenValue);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS));
        verificationToken.setPendingEmail(newEmail);
        tokenRepository.save(verificationToken);

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRecipient(newEmail);
        emailRequest.setSubject("Confirma tu nuevo email en MyPresentPast");
        emailRequest.setName(user.getName() + " " + user.getLastName());
        emailRequest.setVerificationUrl("http://localhost:4200/verify-success?token=" + tokenValue);
        emailRequest.setEmailChange(true);
        emailService.sendMail(emailRequest);
    }

    @Override
    public void resendVerification(String email) throws MessagingException {
        // Buscar el usuario por email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ese email"));

        // Verificar si ya está confirmado
        if (user.isEmailVerified()) {
            throw new BadRequestException("El email ya está verificado");
        }

        // Buscar el token existente
        VerificationToken token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un token de verificación para este usuario"));

        // Validar si está expirado
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El token ha expirado. Por favor, regístrate nuevamente.");
        }

        // Preparar el correo
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRecipient(user.getEmail());
        emailRequest.setSubject("Reenvío: Confirma tu email en MyPresentPast");
        emailRequest.setName(user.getName() + " " + user.getLastName());
        emailRequest.setVerificationUrl("http://localhost:4200/verify-success?token=" + token.getToken());

        // Enviar el correo
        emailService.sendMail(emailRequest);
    }
}
