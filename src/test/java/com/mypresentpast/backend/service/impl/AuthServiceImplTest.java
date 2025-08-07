package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.LoginRequest;
import com.mypresentpast.backend.dto.request.RegisterRequest;
import com.mypresentpast.backend.dto.response.AuthResponse;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.JwtService;
import com.mypresentpast.backend.utils.MessageBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Tests para registrar usuario

    @Test
    void givenValidRegisterRequest_whenRegister_thenReturnToken() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .profileUsername("springmaster")
                .password("Aa12345678")
                .confirmPassword("Aa12345678")
                .name("Spring")
                .lastName("Master")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByProfileUsername(request.getProfileUsername())).thenReturn(false);

        // Simular hash de contraseña
        String hashedPassword = "hashed_Aa12345678";
        when(passwordEncoder.encode("Aa12345678")).thenReturn(hashedPassword);

        User expectedUser = User.builder()
                .email(request.getEmail())
                .profileUsername(request.getProfileUsername())
                .password(hashedPassword)
                .role(UserRole.NORMAL)
                .name(request.getName())
                .lastName(request.getLastName())
                .build();

        when(jwtService.getToken(refEq(expectedUser))).thenReturn("mocked-jwt-token");

        // When
        AuthResponse response = authService.register(request);

        // Then
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());

        verify(userRepository).save(refEq(expectedUser));

    }

    @Test
    void givenExistingEmail_whenRegister_thenThrowDataIntegrityViolationException() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("existing@mail.com")
                .profileUsername("newuser")
                .name("name")
                .lastName("lastname")
                .password("Aa12345678")
                .confirmPassword("Aa12345678")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> authService.register(request)
        );

        assertEquals(String.format(MessageBundle.DUPLICATE_EMAIL, request.getEmail()), exception.getMessage());
    }

    @Test
    void givenExistingProfileUsername_whenRegister_thenThrowDataIntegrityViolationException() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("new@mail.com")
                .profileUsername("takenusername")
                .name("name")
                .lastName("lastname")
                .password("Aa12345678")
                .confirmPassword("Aa12345678")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByProfileUsername(request.getProfileUsername())).thenReturn(true);

        // When & Then
        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> authService.register(request)
        );

        assertEquals(String.format(MessageBundle.DUPLICATE_USERNAME, request.getProfileUsername()), exception.getMessage());
    }

    // Tests para login de usuario

    @Test
    void givenValidCredentials_whenLogin_thenReturnToken() {
        // Given
        String email = "test@example.com";
        String password = "1234";

        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        User user = User.builder()
                .email(email)
                .password("encodedPassword")
                .profileUsername("springmaster")
                .role(UserRole.NORMAL)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.getToken(refEq(user))).thenReturn("mocked-jwt");

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("mocked-jwt", response.getToken());

        // Verify interactions
        verify(authenticationManager).authenticate(
                refEq(new UsernamePasswordAuthenticationToken(email, password))
        );
        verify(userRepository).findByEmail(email);
        verify(jwtService).getToken(refEq(user));
    }

    @Test
    void givenEmailNotFound_whenLogin_thenThrowException() {
        // Given
        String email = "notfound@example.com";

        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password("1234")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> authService.login(request));
    }


}