package com.mypresentpast.backend.dto.response;

import com.mypresentpast.backend.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta del perfil de usuario (público o propio).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private String profileUsername;
    private String name;
    private String lastName;
    private String email;
    private String avatarUrl;
    private UserRole userType;
    private Boolean isSelf;
    private Boolean following;
    private Long postCount;
    private Long followerCount;
    private Long followingCount;
}
