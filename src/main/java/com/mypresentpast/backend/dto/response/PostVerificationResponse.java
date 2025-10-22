package com.mypresentpast.backend.dto.response;

import com.mypresentpast.backend.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de verificación de post.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostVerificationResponse {

    private Long id;
    private Long postId;
    private UserDto verifiedBy;
    private LocalDateTime verifiedAt;
    private Boolean isActive;
}
