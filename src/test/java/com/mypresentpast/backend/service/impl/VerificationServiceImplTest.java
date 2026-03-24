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
import com.mypresentpast.backend.utils.MessageBundle;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceImplTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerificationServiceImpl verificationService;

    private User user;
    private VerificationToken testToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(123L);
        user.setEmail("test@example.com");
        user.setName("John");
        user.setLastName("Doe");
        user.setEmailVerified(false);

        testToken = new VerificationToken();
        testToken.setToken("test-token-123");
        testToken.setUser(user);
        testToken.setExpiryDate(LocalDateTime.now().plusHours(24));
    }

    // Verifica que al crear un token se elimina el anterior y se persiste uno nuevo con expiración de 24h.
    @Test
    void createVerificationToken_ExistingToken_DeletesOldAndCreatesNew() {
        VerificationToken savedToken = new VerificationToken();
        savedToken.setId(999L);
        savedToken.setUser(user);

        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
        when(tokenRepository.save(captor.capture())).thenReturn(savedToken);

        VerificationToken result = verificationService.createVerificationToken(user);

        verify(tokenRepository).deleteByUser(refEq(user));

        VerificationToken saved = captor.getValue();
        assertEquals(user, saved.getUser());
        assertNotNull(saved.getToken());
        assertTrue(saved.getExpiryDate().isAfter(LocalDateTime.now().plusHours(23)));
        assertTrue(saved.getExpiryDate().isBefore(LocalDateTime.now().plusHours(25)));
        assertSame(savedToken, result);
    }

    // Verifica que un token inexistente lanza ResourceNotFoundException con el mensaje correcto.
    @Test
    void validateVerificationToken_TokenNotFound_ThrowsResourceNotFoundException() {
        when(tokenRepository.findByToken("non-existent-token")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> verificationService.validateVerificationToken("non-existent-token"));

        assertEquals(MessageBundle.TOKEN_NOT_FOUND, ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    // Verifica que un token vencido lanza BadRequestException con el mensaje correcto.
    @Test
    void validateVerificationToken_TokenExpired_ThrowsBadRequestException() {
        testToken.setExpiryDate(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("test-token-123")).thenReturn(Optional.of(testToken));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> verificationService.validateVerificationToken("test-token-123"));

        assertEquals(MessageBundle.TOKEN_EXPIRED, ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    // Verifica que un token válido marca el email como verificado, elimina el token y retorna el mensaje correcto.
    @Test
    void validateVerificationToken_ValidToken_SetsEmailVerifiedAndDeletesToken() {
        when(tokenRepository.findByToken("test-token-123")).thenReturn(Optional.of(testToken));

        ApiResponse response = verificationService.validateVerificationToken("test-token-123");

        assertTrue(user.isEmailVerified());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(testToken);
        assertNotNull(response);
        assertEquals("Email confirmado con éxito", response.getMessage());
    }

    // ── resendVerification ─────────────────────────────────────────────────

    // Verifica que el email de verificación se reenvía con los datos correctos cuando todo es válido.
    @Test
    void resendVerification_ValidRequest_SendsEmailWithCorrectData() throws MessagingException {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(user)).thenReturn(Optional.of(testToken));

        EmailRequest expectedEmailRequest = new EmailRequest();
        expectedEmailRequest.setRecipient("test@example.com");
        expectedEmailRequest.setSubject("Reenvío: Confirma tu email en MyPresentPast");
        expectedEmailRequest.setName("John Doe");
        expectedEmailRequest.setVerificationUrl("http://localhost:4200/verify-success?token=test-token-123");

        verificationService.resendVerification("test@example.com");

        verify(emailService).sendMail(refEq(expectedEmailRequest));
    }

    // Verifica que buscar un email inexistente lanza ResourceNotFoundException.
    @Test
    void resendVerification_UserNotFound_ThrowsResourceNotFoundException() throws MessagingException {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> verificationService.resendVerification("unknown@example.com"));

        verify(emailService, never()).sendMail(any());
    }

    // Verifica que intentar reenviar el email a un usuario ya verificado lanza BadRequestException.
    @Test
    void resendVerification_EmailAlreadyVerified_ThrowsBadRequestException() throws MessagingException {
        user.setEmailVerified(true);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> verificationService.resendVerification("test@example.com"));

        verify(emailService, never()).sendMail(any());
    }

    // Verifica que si no existe token para el usuario se lanza ResourceNotFoundException.
    @Test
    void resendVerification_TokenNotFound_ThrowsResourceNotFoundException() throws MessagingException {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> verificationService.resendVerification("test@example.com"));

        verify(emailService, never()).sendMail(any());
    }

    // Verifica que un token expirado lanza BadRequestException sin enviar el email.
    @Test
    void resendVerification_TokenExpired_ThrowsBadRequestException() throws MessagingException {
        testToken.setExpiryDate(LocalDateTime.now().minusHours(1));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(user)).thenReturn(Optional.of(testToken));

        assertThrows(BadRequestException.class,
                () -> verificationService.resendVerification("test@example.com"));

        verify(emailService, never()).sendMail(any());
    }
}