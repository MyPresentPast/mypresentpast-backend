package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.response.LikeStatusResponse;
import com.mypresentpast.backend.dto.response.LikeToggleResponse;

/**
 * Servicio para manejar operaciones de likes en posts.
 */
public interface LikeService {

    /**
     * Alterna el like de un post (si tiene like lo quita, si no lo tiene lo agrega).
     * 
     * @param postId ID del post
     * @return respuesta con el estado actualizado
     */
    LikeToggleResponse toggleLike(Long postId);

    /**
     * Obtiene el estado de like de un post para el usuario actual.
     * 
     * @param postId ID del post
     * @return estado del like y total de likes
     */
    LikeStatusResponse getLikeStatus(Long postId);

    /**
     * Obtiene el número total de likes de un post.
     * 
     * @param postId ID del post
     * @return número total de likes
     */
    Long getTotalLikes(Long postId);

    /**
     * Verifica si el usuario actual dio like a un post.
     * 
     * @param postId ID del post
     * @return true si el usuario dio like, false si no
     */
    Boolean isLikedByCurrentUser(Long postId);
}
