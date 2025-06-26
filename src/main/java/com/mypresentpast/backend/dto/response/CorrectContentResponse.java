package com.mypresentpast.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de corrección de contenido con IA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectContentResponse {

    private String originalContent;
    private String correctedContent;
    private boolean hasChanges;
    private String message;
} 