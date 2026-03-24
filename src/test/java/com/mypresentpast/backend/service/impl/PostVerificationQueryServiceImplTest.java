package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.PostVerification;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.PostVerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostVerificationQueryServiceImplTest {

    @Mock
    private PostVerificationRepository postVerificationRepository;

    @InjectMocks
    private PostVerificationQueryServiceImpl postVerificationQueryService;

    private User institutionAuthor;
    private User normalAuthor;
    private User verifierUser;
    private Post postByInstitution;
    private Post postByNormalUser;

    @BeforeEach
    void setUp() {
        institutionAuthor = User.builder()
                .id(10L)
                .profileUsername("institution_user")
                .role(UserRole.INSTITUTION)
                .build();

        normalAuthor = User.builder()
                .id(20L)
                .profileUsername("normal_user")
                .role(UserRole.NORMAL)
                .build();

        verifierUser = User.builder()
                .id(30L)
                .profileUsername("verifier_institution")
                .role(UserRole.INSTITUTION)
                .build();

        postByInstitution = Post.builder()
                .id(1L)
                .author(institutionAuthor)
                .build();

        postByNormalUser = Post.builder()
                .id(2L)
                .author(normalAuthor)
                .build();
    }

    // ── isPostVerified ─────────────────────────────────────────────────────

    // Post cuyo autor tiene rol INSTITUTION se considera auto-verificado; no consulta el repositorio.
    @Test
    void isPostVerified_AuthorIsInstitution_ReturnsTrueWithoutQueryingRepository() {
        boolean result = postVerificationQueryService.isPostVerified(postByInstitution);

        assertTrue(result);
        verifyNoInteractions(postVerificationRepository);
    }

    // Post de usuario NORMAL con verificación activa en repositorio debe retornar true.
    @Test
    void isPostVerified_AuthorIsNormalAndActiveVerificationExists_ReturnsTrue() {
        when(postVerificationRepository.existsActiveByPostId(2L)).thenReturn(true);

        boolean result = postVerificationQueryService.isPostVerified(postByNormalUser);

        assertTrue(result);
        verify(postVerificationRepository).existsActiveByPostId(2L);
    }

    // Post de usuario NORMAL sin verificación activa debe retornar false.
    @Test
    void isPostVerified_AuthorIsNormalAndNoActiveVerification_ReturnsFalse() {
        when(postVerificationRepository.existsActiveByPostId(2L)).thenReturn(false);

        boolean result = postVerificationQueryService.isPostVerified(postByNormalUser);

        assertFalse(result);
        verify(postVerificationRepository).existsActiveByPostId(2L);
    }

    // Post sin autor no activa la regla de auto-verificación; delega al repositorio.
    @Test
    void isPostVerified_AuthorIsNull_DelegatesToRepositoryAndReturnsTrue() {
        Post postWithNullAuthor = Post.builder().id(3L).author(null).build();
        when(postVerificationRepository.existsActiveByPostId(3L)).thenReturn(true);

        boolean result = postVerificationQueryService.isPostVerified(postWithNullAuthor);

        assertTrue(result);
        verify(postVerificationRepository).existsActiveByPostId(3L);
    }

    // ── getActiveVerification ──────────────────────────────────────────────

    // Cuando el repositorio encuentra una verificación activa, retorna Optional con la verificación.
    @Test
    void getActiveVerification_ActiveVerificationExists_ReturnsNonEmptyOptional() {
        PostVerification verification = PostVerification.builder()
                .id(100L)
                .post(postByNormalUser)
                .verifiedBy(verifierUser)
                .verifiedAt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .build();
        when(postVerificationRepository.findActiveByPostId(2L)).thenReturn(Optional.of(verification));

        Optional<PostVerification> result = postVerificationQueryService.getActiveVerification(2L);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getId());
        assertEquals(verifierUser, result.get().getVerifiedBy());
        verify(postVerificationRepository).findActiveByPostId(2L);
    }

    // Cuando el repositorio no encuentra verificación activa, retorna Optional vacío.
    @Test
    void getActiveVerification_NoActiveVerification_ReturnsEmptyOptional() {
        when(postVerificationRepository.findActiveByPostId(2L)).thenReturn(Optional.empty());

        Optional<PostVerification> result = postVerificationQueryService.getActiveVerification(2L);

        assertFalse(result.isPresent());
        verify(postVerificationRepository).findActiveByPostId(2L);
    }

    // ── getExternalVerifier ────────────────────────────────────────────────

    // Cuando existe verificación activa, retorna el usuario que verificó el post.
    @Test
    void getExternalVerifier_ActiveVerificationExists_ReturnsVerifiedByUser() {
        PostVerification verification = PostVerification.builder()
                .id(100L)
                .post(postByNormalUser)
                .verifiedBy(verifierUser)
                .verifiedAt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .build();
        when(postVerificationRepository.findActiveByPostId(2L)).thenReturn(Optional.of(verification));

        User result = postVerificationQueryService.getExternalVerifier(2L);

        assertNotNull(result);
        assertEquals(30L, result.getId());
        assertEquals("verifier_institution", result.getProfileUsername());
    }

    // Cuando no existe verificación activa, retorna null indicando que no hay verificador externo.
    @Test
    void getExternalVerifier_NoActiveVerification_ReturnsNull() {
        when(postVerificationRepository.findActiveByPostId(2L)).thenReturn(Optional.empty());

        User result = postVerificationQueryService.getExternalVerifier(2L);

        assertNull(result);
    }
}
