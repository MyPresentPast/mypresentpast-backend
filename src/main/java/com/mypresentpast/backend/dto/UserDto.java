package com.mypresentpast.backend.dto;

import com.mypresentpast.backend.enums.UserRol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for User information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private UserRol type;
    private String avatar;
}
