package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.response.LikeStatusResponse;
import com.mypresentpast.backend.dto.response.LikeToggleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para operaciones relacionadas con likes de posts.
 */
@RequestMapping("/posts")
public interface LikeController {

    /**
     * Alterna el like de un post (toggle).
     * Si el usuario ya dio like, lo quita. Si no dio like, lo agrega.
     * 
     * @param postId ID del post
     * @return respuesta con el estado actualizado del like
     */
    @PostMapping("/{postId}/like")
    ResponseEntity<LikeToggleResponse> toggleLike(@PathVariable Long postId);

    /**
     * Obtiene el estado de like de un post para el usuario actual.
     * 
     * @param postId ID del post
     * @return estado del like y número total de likes
     */
    @GetMapping("/{postId}/like/status")
    ResponseEntity<LikeStatusResponse> getLikeStatus(@PathVariable Long postId);

    /**
     * Obtiene solo el número total de likes de un post.
     * 
     * @param postId ID del post
     * @return número total de likes
     */
    @GetMapping("/{postId}/likes/count")
    ResponseEntity<Long> getTotalLikes(@PathVariable Long postId);
}
