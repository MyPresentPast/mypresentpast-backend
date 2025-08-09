package com.mypresentpast.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para las estadísticas de seguimiento de un usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatsResponse {
    private Long userId;
    private String profileUsername;
    private long followersCount;
    private long followingCount;
    private boolean isFollowing; // Si el usuario actual sigue a este usuario
}