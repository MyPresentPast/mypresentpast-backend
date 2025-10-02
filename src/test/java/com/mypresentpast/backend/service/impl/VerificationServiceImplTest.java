package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.VerificationToken;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.repository.VerificationTokenRepository;
import com.mypresentpast.backend.utils.MessageBundle;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceImplTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VerificationServiceImpl verificationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(123L);
        user.setEmail("test@example.com");
        user.setEmailVerified(false);
    }

    @Test
    void testCreateVerificationToken_whenExistingToken_thenDeletesOldAndCreatesNew() {
        // Arrange
        VerificationToken savedToken = new VerificationToken();
        savedToken.setId(999L);
        savedToken.setUser(user);

        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
        when(tokenRepository.save(captor.capture())).thenReturn(savedToken);

        // Act
        VerificationToken result = verificationService.createVerificationToken(user);

        // Assert
        verify(tokenRepository, times(1)).deleteByUser(user);
        verify(tokenRepository, times(1)).save(any(VerificationToken.class));

        VerificationToken tokenArg = captor.getValue();
        assertEquals(user, tokenArg.getUser());
        assertNotNull(tokenArg.getToken());

        LocalDateTime expiry = tokenArg.getExpiryDate();
        LocalDateTime now = LocalDateTime.now();
        assertTrue(expiry.isAfter(now.plusHours(23)));
        assertTrue(expiry.isBefore(now.plusHours(25)));

        assertSame(savedToken, result);
    }

    @Test
    void testValidateVerificationToken_tokenNotFound_throwsResourceNotFoundException() {
        // Arrange
        String fakeToken = "non-existent-token";
        when(tokenRepository.findByToken(fakeToken)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            verificationService.validateVerificationToken(fakeToken);
        });

        assertEquals(MessageBundle.TOKEN_NOT_FOUND, ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void testValidateVerificationToken_tokenExpired_throwsBadRequestException() {
        // Arrange
        String tokenString = "expired-token";
        VerificationToken vToken = new VerificationToken();
        vToken.setToken(tokenString);
        vToken.setUser(user);
        vToken.setExpiryDate(LocalDateTime.now().minusHours(1)); // Token vencido

        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.of(vToken));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            verificationService.validateVerificationToken(tokenString);
        });

        assertEquals(MessageBundle.TOKEN_EXPIRED, ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void testValidateVerificationToken_validToken_setsEmailVerifiedAndDeletesToken() {
        // Arrange
        String validToken = "valid-token-123";
        VerificationToken vToken = new VerificationToken();
        vToken.setToken(validToken);
        vToken.setUser(user);
        vToken.setExpiryDate(LocalDateTime.now().plusHours(2)); // Token válido

        when(tokenRepository.findByToken(validToken)).thenReturn(Optional.of(vToken));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        ApiResponse response = verificationService.validateVerificationToken(validToken);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertTrue(savedUser.isEmailVerified());

        verify(tokenRepository).delete(vToken);

        assertNotNull(response);
        assertEquals("Email confirmado con éxito", response.getMessage());
    }


}