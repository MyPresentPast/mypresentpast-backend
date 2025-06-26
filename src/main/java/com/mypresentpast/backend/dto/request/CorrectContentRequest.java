package com.mypresentpast.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar corrección de contenido con IA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectContentRequest {

    @NotBlank()
    private String content;
} 