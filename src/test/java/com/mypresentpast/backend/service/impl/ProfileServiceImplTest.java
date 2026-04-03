package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.ProfileUpdateRequest;
import com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest;
import com.mypresentpast.backend.dto.response.ProfileResponse;
import com.mypresentpast.backend.dto.response.ProfileUpdateResponse;
import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.exception.UnauthorizedException;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.FollowRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.CloudinaryService;
import com.mypresentpast.backend.service.JwtService;
import com.mypresentpast.backend.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    // ── Mocks ──────────────────────────────────────────────────────────────
    @Mock
    private UserRepository userRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    // ── Datos de test reutilizables ────────────────────────────────────────
    private User testUser;

    // ── Setup ──────────────────────────────────────────────────────────────
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .profileUsername("testuser")
                .name("John")
                .lastName("Doe")
                .role(UserRole.NORMAL)
                .emailVerified(true)
                .avatar("https://cloudinary.com/avatar.jpg")
                .password("encodedPassword")
                .build();
    }

    // ── getProfile ─────────────────────────────────────────────────────────

    // Verifica que el usuario autenticado viendo su propio perfil recibe el email y isSelf=true.
    @Test
    void getProfile_AuthenticatedUserViewingOwnProfile_ReturnsSelfProfileWithEmail() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(followRepository.countByFolloweeId(1L)).thenReturn(10L);
            when(followRepository.countByFollowerId(1L)).thenReturn(5L);
            when(postRepository.countByAuthorIdAndStatus(1L, PostStatus.ACTIVE)).thenReturn(3L);

            ProfileResponse response = profileService.getProfile(1L);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("testuser", response.getProfileUsername());
            assertEquals("test@example.com", response.getEmail()); // email incluido por ser perfil propio
            assertTrue(response.getIsSelf());
            assertFalse(response.getFollowing()); // no puede seguirse a sí mismo
            assertEquals(10L, response.getFollowerCount());
            assertEquals(5L, response.getFollowingCount());
            assertEquals(3L, response.getPostCount());
            verify(followRepository, never()).existsByFollowerIdAndFolloweeId(any(), any());
        }
    }

    // Verifica que un usuario autenticado viendo otro perfil no recibe el email y se consulta isFollowing.
    @Test
    void getProfile_AuthenticatedUserViewingOtherProfile_ReturnsProfileWithoutEmail() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(2L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(followRepository.existsByFollowerIdAndFolloweeId(2L, 1L)).thenReturn(true);
            when(followRepository.countByFolloweeId(1L)).thenReturn(10L);
            when(followRepository.countByFollowerId(1L)).thenReturn(5L);
            when(postRepository.countByAuthorIdAndStatus(1L, PostStatus.ACTIVE)).thenReturn(3L);

            ProfileResponse response = profileService.getProfile(1L);

            assertNotNull(response);
            assertNull(response.getEmail()); // email no incluido por ser perfil ajeno
            assertFalse(response.getIsSelf());
            assertTrue(response.getFollowing());
            verify(followRepository).existsByFollowerIdAndFolloweeId(2L, 1L);
        }
    }

    // Verifica que un usuario no autenticado accede al perfil público sin email y sin check de following.
    @Test
    void getProfile_UnauthenticatedUser_ReturnsPublicProfileWithoutEmail() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenThrow(new UnauthorizedException("no auth"));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(followRepository.countByFolloweeId(1L)).thenReturn(10L);
            when(followRepository.countByFollowerId(1L)).thenReturn(5L);
            when(postRepository.countByAuthorIdAndStatus(1L, PostStatus.ACTIVE)).thenReturn(3L);

            ProfileResponse response = profileService.getProfile(1L);

            assertNotNull(response);
            assertNull(response.getEmail());
            assertFalse(response.getIsSelf());
            assertFalse(response.getFollowing());
            verify(followRepository, never()).existsByFollowerIdAndFolloweeId(any(), any());
        }
    }

    // Verifica que un userId inválido (null o <= 0) lanza IllegalArgumentException antes de consultar la DB.
    @Test
    void getProfile_InvalidUserId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> profileService.getProfile(0L));
        assertThrows(IllegalArgumentException.class, () -> profileService.getProfile(-1L));
        assertThrows(IllegalArgumentException.class, () -> profileService.getProfile(null));
        verify(userRepository, never()).findById(any());
    }

    // Verifica que buscar el perfil de un usuario inexistente lanza ResourceNotFoundException.
    @Test
    void getProfile_UserNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> profileService.getProfile(99L));
        }
    }

    // ── updateProfile ──────────────────────────────────────────────────────

    // Verifica que los campos del perfil se actualizan correctamente y se retorna un nuevo JWT.
    @Test
    void updateProfile_ValidData_UpdatesFieldsAndReturnsNewToken() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                    .email("new@example.com")
                    .profileUsername("newuser")
                    .name("Jane")
                    .lastName("Smith")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.existsByProfileUsername("newuser")).thenReturn(false);
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(jwtService.getToken(testUser)).thenReturn("new-jwt-token");

            ProfileUpdateResponse response = profileService.updateProfile(request);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("newuser", response.getProfileUsername());
            assertEquals("new@example.com", response.getEmail());
            assertEquals("new-jwt-token", response.getToken());
            verify(userRepository).save(testUser);
        }
    }

    // Verifica que actualizar el perfil de un usuario inexistente lanza ResourceNotFoundException.
    @Test
    void updateProfile_UserNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            ProfileUpdateRequest request = ProfileUpdateRequest.builder().build();
            assertThrows(ResourceNotFoundException.class,
                    () -> profileService.updateProfile(request));

            verify(userRepository, never()).save(any());
        }
    }

    // Verifica que cambiar a un email ya registrado lanza DataIntegrityViolationException.
    @Test
    void updateProfile_DuplicateEmail_ThrowsDataIntegrityViolationException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

            ProfileUpdateRequest request = ProfileUpdateRequest.builder().email("taken@example.com").build();
            assertThrows(DataIntegrityViolationException.class,
                    () -> profileService.updateProfile(request));

            verify(userRepository, never()).save(any());
        }
    }

    // Verifica que cambiar a un username ya registrado lanza DataIntegrityViolationException.
    @Test
    void updateProfile_DuplicateUsername_ThrowsDataIntegrityViolationException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByProfileUsername("takenuser")).thenReturn(true);

            ProfileUpdateRequest request = ProfileUpdateRequest.builder().profileUsername("takenuser").build();
            assertThrows(DataIntegrityViolationException.class,
                    () -> profileService.updateProfile(request));

            verify(userRepository, never()).save(any());
        }
    }

    // ── changePassword ─────────────────────────────────────────────────────

    // Verifica que la nueva contraseña se codifica y se persiste cuando los datos son válidos.
    @Test
    void changePassword_ValidData_EncodesAndSavesNewPassword() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPass")
                .newPassword("NewPassword123")
                .confirmPassword("NewPassword123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("NewPassword123", "encodedPassword")).thenReturn(false);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encodedNewPassword");

        profileService.changePassword(1L, request);

        assertEquals("encodedNewPassword", testUser.getPassword());
        verify(userRepository).save(testUser);
    }

    // Verifica que confirmar una contraseña distinta lanza BadRequestException sin consultar la DB.
    @Test
    void changePassword_PasswordMismatch_ThrowsBadRequestException() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPass")
                .newPassword("NewPassword123")
                .confirmPassword("DifferentPassword")
                .build();

        assertThrows(BadRequestException.class, () -> profileService.changePassword(1L, request));
        verify(userRepository, never()).findById(any());
    }

    // Verifica que una contraseña nueva que no cumple los requisitos lanza BadRequestException sin consultar la DB.
    @Test
    void changePassword_InvalidNewPassword_ThrowsBadRequestException() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPass")
                .newPassword("bad")
                .confirmPassword("bad")
                .build();

        assertThrows(BadRequestException.class, () -> profileService.changePassword(1L, request));
        verify(userRepository, never()).findById(any());
    }

    // Verifica que cambiar contraseña de un usuario inexistente lanza ResourceNotFoundException.
    @Test
    void changePassword_UserNotFound_ThrowsResourceNotFoundException() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPass")
                .newPassword("NewPassword123")
                .confirmPassword("NewPassword123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> profileService.changePassword(1L, request));

        verify(userRepository, never()).save(any());
    }

    // Verifica que una contraseña actual incorrecta lanza BadRequestException.
    @Test
    void changePassword_CurrentPasswordInvalid_ThrowsBadRequestException() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("wrongPass")
                .newPassword("NewPassword123")
                .confirmPassword("NewPassword123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> profileService.changePassword(1L, request));
        verify(userRepository, never()).save(any());
    }

    // Verifica que usar la misma contraseña actual como nueva lanza BadRequestException.
    @Test
    void changePassword_NewPasswordSameAsOld_ThrowsBadRequestException() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPass")
                .newPassword("NewPassword123")
                .confirmPassword("NewPassword123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("NewPassword123", "encodedPassword")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> profileService.changePassword(1L, request));
        verify(userRepository, never()).save(any());
    }

    // ── uploadAvatar ───────────────────────────────────────────────────────

    // Verifica que el avatar se sube a Cloudinary y la URL resultante se persiste en el usuario.
    @Test
    void uploadAvatar_ValidFile_UpdatesAvatarUrlAndReturnsIt() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            MultipartFile file = mock(MultipartFile.class);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(cloudinaryService.uploadAvatar(file, 1L))
                    .thenReturn(Map.of("secure_url", "https://cloudinary.com/new-avatar.jpg"));

            String resultUrl = profileService.uploadAvatar(file);

            assertEquals("https://cloudinary.com/new-avatar.jpg", resultUrl);
            assertEquals("https://cloudinary.com/new-avatar.jpg", testUser.getAvatar());
            verify(cloudinaryService).uploadAvatar(file, 1L);
            verify(userRepository).save(testUser);
        }
    }

    // Verifica que subir avatar para un usuario inexistente lanza ResourceNotFoundException.
    @Test
    void uploadAvatar_UserNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> profileService.uploadAvatar(mock(MultipartFile.class)));

            verify(cloudinaryService, never()).uploadAvatar(any(), any());
        }
    }
}
