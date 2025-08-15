package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.ProfileUpdateRequest;
import com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest;
import com.mypresentpast.backend.dto.response.ProfileUpdateResponse;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.FollowRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfileServiceImplTest {

    @InjectMocks
    private ProfileServiceImpl systemUnderTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void updateProfile_validData_userUpdated() {
        // Arrange
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .email("new@email.com")
                .profileUsername("newUser")
                .name("John")
                .lastName("Doe")
                .build();

        User existingUser = User.builder()
                .id(10L)
                .email("old@email.com")
                .profileUsername("oldUser")
                .name("Old")
                .lastName("Name")
                .build();

        // Mock SecurityUtils.getCurrentUserId()
        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> securityUtilsMock = Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            securityUtilsMock.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId).thenReturn(10L);

            when(userRepository.findById(10L)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
            when(userRepository.existsByProfileUsername("newUser")).thenReturn(false);
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            ProfileUpdateResponse response = systemUnderTest.updateProfile(request);

            // Assert
            assertNotNull(response);
            assertEquals(10L, response.getId());
            assertEquals("newUser", response.getProfileUsername());
            assertEquals("new@email.com", response.getEmail());
            assertEquals("John", response.getName());
            assertEquals("Doe", response.getLastName());

            User savedUser = userCaptor.getValue();
            assertEquals("newUser", savedUser.getProfileUsername());
            assertEquals("new@email.com", savedUser.getEmail());
            assertEquals("John", savedUser.getName());
            assertEquals("Doe", savedUser.getLastName());
        }
    }

    @Test
    void updateProfile_duplicateEmail_throwsException() {
        // Arrange
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .email("duplicate@email.com")
                .build();

        User existingUser = User.builder()
                .id(20L)
                .email("old@email.com")
                .profileUsername("user")
                .build();

    }

    @Test
    void changePassword_validData_passwordChanged() {
        // Arrange
        Long userId = 40L;
        com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest request =
                ChangePasswordRequest.builder()
                        .currentPassword("oldPass")
                        .newPassword("NewPassword123")
                        .confirmPassword("NewPassword123")
                        .build();

        User user = User.builder()
                .id(userId)
                .password("encodedOldPass")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        try (MockedStatic<com.mypresentpast.backend.utils.CommonFunctions> commonFunctionsMock = Mockito.mockStatic(com.mypresentpast.backend.utils.CommonFunctions.class)) {
            commonFunctionsMock.when(() -> com.mypresentpast.backend.utils.CommonFunctions.isValidPassword("NewPassword123")).thenReturn(true);
            when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
            when(passwordEncoder.matches("NewPassword123", "encodedOldPass")).thenReturn(false);
            when(passwordEncoder.encode("NewPassword123")).thenReturn("encodedNewPass");

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            systemUnderTest.changePassword(userId, request);

            // Assert
            User savedUser = userCaptor.getValue();
            assertEquals("encodedNewPass", savedUser.getPassword());
        }
    }

    @Test
    void changePassword_passwordMismatch_throwsException() {
        // Arrange
        Long userId = 50L;
        com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest request =
                com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest.builder()
                        .currentPassword("oldPass")
                        .newPassword("NewPassword123")
                        .confirmPassword("DifferentPassword")
                        .build();

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(com.mypresentpast.backend.exception.BadRequestException.class, () -> {
            systemUnderTest.changePassword(userId, request);
        });
    }

    @Test
    void changePassword_invalidPassword_throwsException() {
        // Arrange
        Long userId = 60L;
        com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest request =
                com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest.builder()
                        .currentPassword("oldPass")
                        .newPassword("bad")
                        .confirmPassword("bad")
                        .build();

        try (MockedStatic<com.mypresentpast.backend.utils.CommonFunctions> commonFunctionsMock = Mockito.mockStatic(com.mypresentpast.backend.utils.CommonFunctions.class)) {
            commonFunctionsMock.when(() -> com.mypresentpast.backend.utils.CommonFunctions.isValidPassword("bad")).thenReturn(false);

            // Act & Assert
            org.junit.jupiter.api.Assertions.assertThrows(com.mypresentpast.backend.exception.BadRequestException.class, () -> {
                systemUnderTest.changePassword(userId, request);
            });
        }
    }

    @Test
    void changePassword_currentPasswordInvalid_throwsException() {
        // Arrange
        Long userId = 70L;
        com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest request =
                com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest.builder()
                        .currentPassword("wrongPass")
                        .newPassword("NewPassword123")
                        .confirmPassword("NewPassword123")
                        .build();

        User user = User.builder()
                .id(userId)
                .password("encodedOldPass")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        try (MockedStatic<com.mypresentpast.backend.utils.CommonFunctions> commonFunctionsMock = Mockito.mockStatic(com.mypresentpast.backend.utils.CommonFunctions.class)) {
            commonFunctionsMock.when(() -> com.mypresentpast.backend.utils.CommonFunctions.isValidPassword("NewPassword123")).thenReturn(true);
            when(passwordEncoder.matches("wrongPass", "encodedOldPass")).thenReturn(false);

            // Act & Assert
            org.junit.jupiter.api.Assertions.assertThrows(com.mypresentpast.backend.exception.BadRequestException.class, () -> {
                systemUnderTest.changePassword(userId, request);
            });
        }
    }

    @Test
    void changePassword_newPasswordSameAsOld_throwsException() {
        // Arrange
        Long userId = 80L;
        com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest request =
                com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest.builder()
                        .currentPassword("oldPass")
                        .newPassword("NewPassword123")
                        .confirmPassword("NewPassword123")
                        .build();

        User user = User.builder()
                .id(userId)
                .password("encodedOldPass")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        try (MockedStatic<com.mypresentpast.backend.utils.CommonFunctions> commonFunctionsMock = Mockito.mockStatic(com.mypresentpast.backend.utils.CommonFunctions.class)) {
            commonFunctionsMock.when(() -> com.mypresentpast.backend.utils.CommonFunctions.isValidPassword("NewPassword123")).thenReturn(true);
            when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
            when(passwordEncoder.matches("NewPassword123", "encodedOldPass")).thenReturn(true);

            // Act & Assert
            org.junit.jupiter.api.Assertions.assertThrows(com.mypresentpast.backend.exception.BadRequestException.class, () -> {
                systemUnderTest.changePassword(userId, request);
            });
        }
    }


    @Test
    void uploadAvatar_validFile_avatarUrlReturnedAndUserUpdated() {
        // Arrange
        Long userId = 100L;
        User user = User.builder()
                .id(userId)
                .avatar(null)
                .build();

        MultipartFile file = Mockito.mock(MultipartFile.class);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/avatar.jpg");

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> securityUtilsMock = Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            securityUtilsMock.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId).thenReturn(userId);

            when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));

            // Mock CloudinaryService
            CloudinaryService cloudinaryService = Mockito.mock(CloudinaryService.class);
            // Reemplaza el cloudinaryService en el systemUnderTest si es necesario
            // Puedes usar ReflectionTestUtils si no tienes setter
            java.lang.reflect.Field field = systemUnderTest.getClass().getDeclaredField("cloudinaryService");
            field.setAccessible(true);
            field.set(systemUnderTest, cloudinaryService);

            when(cloudinaryService.uploadAvatar(eq(file), eq(userId))).thenReturn(uploadResult);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            String resultUrl = systemUnderTest.uploadAvatar(file);

            // Assert
            assertEquals("https://cloudinary.com/avatar.jpg", resultUrl);
            User savedUser = userCaptor.getValue();
            assertEquals("https://cloudinary.com/avatar.jpg", savedUser.getAvatar());

            verify(cloudinaryService).uploadAvatar(eq(file), eq(userId));
            verify(userRepository).save(userCaptor.getValue());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void uploadAvatar_userNotFound_throwsException() {
        // Arrange
        Long userId = 200L;
        MultipartFile file = Mockito.mock(MultipartFile.class);

        try (MockedStatic<com.mypresentpast.backend.utils.SecurityUtils> securityUtilsMock = Mockito.mockStatic(com.mypresentpast.backend.utils.SecurityUtils.class)) {
            securityUtilsMock.when(com.mypresentpast.backend.utils.SecurityUtils::getCurrentUserId).thenReturn(userId);

            when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(com.mypresentpast.backend.exception.ResourceNotFoundException.class, () -> {
                systemUnderTest.uploadAvatar(file);
            });
        }
    }
}
