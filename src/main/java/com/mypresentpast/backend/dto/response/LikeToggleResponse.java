package com.mypresentpast.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta al dar/quitar like a un post.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeToggleResponse {

    /**
     * Mensaje descriptivo de la acción realizada.
     */
    private String message;

    /**
     * Indica si el usuario actual tiene like en el post después de la acción.
     */
    private Boolean isLiked;

    /**
     * Número total de likes del post después de la acción.
     */
    private Long totalLikes;
}
