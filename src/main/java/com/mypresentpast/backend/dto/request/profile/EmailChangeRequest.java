package com.mypresentpast.backend.dto.request.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailChangeRequest {
    @Email
    @NotBlank
    private String newEmail;

    @NotBlank
    private String password;
}
