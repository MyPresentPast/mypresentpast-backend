package com.mypresentpast.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta del estado de like de un post.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeStatusResponse {

    /**
     * Indica si el usuario actual dio like al post.
     */
    private Boolean isLiked;

    /**
     * Número total de likes del post.
     */
    private Long totalLikes;
}
