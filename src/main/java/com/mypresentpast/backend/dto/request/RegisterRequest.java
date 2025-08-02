package com.mypresentpast.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String profileUsername;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String lastName;
}
