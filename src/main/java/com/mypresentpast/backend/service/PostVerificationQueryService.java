package com.mypresentpast.backend.service;

import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.PostVerification;
import com.mypresentpast.backend.model.User;

import java.util.Optional;

/**
 * Servicio para consultas relacionadas con verificación de posts.
 * Separado del servicio principal para mantener responsabilidades claras.
 */
public interface PostVerificationQueryService {

    /**
     * Determina si un post está verificado.
     * Un post está verificado si:
     * 1. Su autor es una INSTITUTION (auto-verificado), O
     * 2. Tiene una verificación activa de otra institución
     */
    boolean isPostVerified(Post post);

    /**
     * Obtiene la verificación activa de un post (si existe).
     */
    Optional<PostVerification> getActiveVerification(Long postId);

    /**
     * Obtiene el usuario que verificó el post externamente.
     * Retorna null si está auto-verificado (autor institución) o no verificado.
     */
    User getExternalVerifier(Long postId);
}
