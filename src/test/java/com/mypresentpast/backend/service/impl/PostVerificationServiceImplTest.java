package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.exception.UnauthorizedException;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.PostVerification;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.PostVerificationRepository;
import com.mypresentpast.backend.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostVerificationServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostVerificationRepository postVerificationRepository;

    @InjectMocks
    private PostVerificationServiceImpl postVerificationService;

    private User institutionUser;
    private User normalUser;
    private User postAuthor;
    private Post testPost;
    private PostVerification activeVerification;

    @BeforeEach
    void setUp() {
        institutionUser = User.builder()
                .id(1L)
                .email("institution@example.com")
                .profileUsername("institution_org")
                .role(UserRole.INSTITUTION)
                .build();

        normalUser = User.builder()
                .id(2L)
                .email("normal@example.com")
                .profileUsername("normal_user")
                .role(UserRole.NORMAL)
                .build();

        postAuthor = User.builder()
                .id(99L)
                .email("author@example.com")
                .profileUsername("post_author")
                .role(UserRole.NORMAL)
                .build();

        testPost = Post.builder()
                .id(10L)
                .author(postAuthor)
                .build();

        activeVerification = PostVerification.builder()
                .id(100L)
                .post(testPost)
                .verifiedBy(institutionUser)
                .verifiedAt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .build();
    }

    // ── verifyPost ─────────────────────────────────────────────────────────

    // Una institución verifica exitosamente el post de otro usuario y recibe el mensaje de éxito.
    @Test
    void verifyPost_InstitutionUserVerifiesAnotherUserPost_ReturnsSuccessMessage() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(institutionUser);
            when(postRepository.findByIdWithRelations(10L)).thenReturn(Optional.of(testPost));
            when(postVerificationRepository.existsActiveByPostId(10L)).thenReturn(false);
            when(postVerificationRepository.save(any())).thenReturn(activeVerification);

            ApiResponse response = postVerificationService.verifyPost(10L);

            assertEquals("Post verificado exitosamente", response.getMessage());
        }
    }

    // La verificación guardada debe tener el post correcto, el usuario verificador, isActive=true y verifiedAt reciente.
    @Test
    void verifyPost_InstitutionUserVerifiesPost_PersistsVerificationWithCorrectFields() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(institutionUser);
            when(postRepository.findByIdWithRelations(10L)).thenReturn(Optional.of(testPost));
            when(postVerificationRepository.existsActiveByPostId(10L)).thenReturn(false);
            when(postVerificationRepository.save(any())).thenReturn(activeVerification);

            postVerificationService.verifyPost(10L);

            ArgumentCaptor<PostVerification> captor = ArgumentCaptor.forClass(PostVerification.class);
            verify(postVerificationRepository).save(captor.capture());
            PostVerification saved = captor.getValue();

            assertEquals(testPost, saved.getPost());
            assertEquals(institutionUser, saved.getVerifiedBy());
            assertTrue(saved.getIsActive());
            assertTrue(saved.getVerifiedAt().isAfter(LocalDateTime.now().minusSeconds(5)));
        }
    }

    // Un usuario con rol NORMAL no puede verificar posts; se lanza UnauthorizedException antes de consultar repos.
    @Test
    void verifyPost_NormalUserAttempts_ThrowsUnauthorizedException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(normalUser);

            assertThrows(UnauthorizedException.class, () -> postVerificationService.verifyPost(10L));

            verifyNoInteractions(postRepository);
            verifyNoInteractions(postVerificationRepository);
        }
    }

    // Un usuario con rol ADMIN no puede verificar posts; se lanza UnauthorizedException.
    @Test
    void verifyPost_AdminUserAttempts_ThrowsUnauthorizedException() {
        User adminUser = User.builder().id(3L).email("admin@example.com").role(UserRole.ADMIN).build();
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(adminUser);

            assertThrows(UnauthorizedException.class, () -> postVerificationService.verifyPost(10L));
        }
    }

    // Si el post no existe en el repositorio se lanza ResourceNotFoundException.
    @Test
    void verifyPost_PostNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(institutionUser);
            when(postRepository.findByIdWithRelations(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> postVerificationService.verifyPost(10L));
        }
    }

    // Una institución no puede verificar sus propios posts; se lanza BadRequestException.
    @Test
    void verifyPost_InstitutionVerifiesOwnPost_ThrowsBadRequestException() {
        Post ownPost = Post.builder().id(10L).author(institutionUser).build();
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(institutionUser);
            when(postRepository.findByIdWithRelations(10L)).thenReturn(Optional.of(ownPost));

            assertThrows(BadRequestException.class, () -> postVerificationService.verifyPost(10L));

            verifyNoInteractions(postVerificationRepository);
        }
    }

    // Si el post ya tiene una verificación activa se lanza BadRequestException.
    @Test
    void verifyPost_PostAlreadyVerified_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(institutionUser);
            when(postRepository.findByIdWithRelations(10L)).thenReturn(Optional.of(testPost));
            when(postVerificationRepository.existsActiveByPostId(10L)).thenReturn(true);

            assertThrows(BadRequestException.class, () -> postVerificationService.verifyPost(10L));

            verify(postVerificationRepository, never()).save(any());
        }
    }

    // ── unverifyPost ───────────────────────────────────────────────────────

    // Una institución desverifica el post que ella misma verificó y recibe el mensaje de éxito.
    @Test
    void unverifyPost_InstitutionUserUnverifiesOwnVerification_ReturnsSuccessMessage() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(institutionUser);
            when(postVerificationRepository.findActiveByPostIdAndVerifiedBy(10L, 1L))
                    .thenReturn(Optional.of(activeVerification));

            ApiResponse response = postVerificationService.unverifyPost(10L);

            assertEquals("Verificación removida exitosamente", response.getMessage());
        }
    }

    // Al desverificar, el campo isActive de la verificación debe quedar en false y guardarse.
    @Test
    void unverifyPost_InstitutionUserUnverifiesPost_SetsIsActiveFalseAndSaves() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(institutionUser);
            when(postVerificationRepository.findActiveByPostIdAndVerifiedBy(10L, 1L))
                    .thenReturn(Optional.of(activeVerification));

            postVerificationService.unverifyPost(10L);

            assertFalse(activeVerification.getIsActive());
            verify(postVerificationRepository).save(activeVerification);
        }
    }

    // Un usuario con rol NORMAL no puede desverificar posts; se lanza UnauthorizedException.
    @Test
    void unverifyPost_NormalUserAttempts_ThrowsUnauthorizedException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(normalUser);

            assertThrows(UnauthorizedException.class, () -> postVerificationService.unverifyPost(10L));

            verifyNoInteractions(postVerificationRepository);
        }
    }

    // Un usuario con rol ADMIN no puede desverificar posts; se lanza UnauthorizedException.
    @Test
    void unverifyPost_AdminUserAttempts_ThrowsUnauthorizedException() {
        User adminUser = User.builder().id(3L).email("admin@example.com").role(UserRole.ADMIN).build();
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(adminUser);

            assertThrows(UnauthorizedException.class, () -> postVerificationService.unverifyPost(10L));
        }
    }

    // Si la institución actual no verificó el post, se lanza BadRequestException.
    @Test
    void unverifyPost_NoActiveVerificationByCurrentInstitution_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(institutionUser);
            when(postVerificationRepository.findActiveByPostIdAndVerifiedBy(10L, 1L))
                    .thenReturn(Optional.empty());

            assertThrows(BadRequestException.class, () -> postVerificationService.unverifyPost(10L));

            verify(postVerificationRepository, never()).save(any());
        }
    }
}
