package com.mypresentpast.backend.dto.response;

import com.mypresentpast.backend.enums.Category;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de generación de publicación con IA.
 * Contiene el contenido generado por la IA listo para crear la publicación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePostResponse {

    private String title;
    private String content;
    private Category category;
    private LocalDate date;
    private Double latitude;
    private Double longitude;
    private String address;
    private String context;
    private String message;
    private boolean success;
}
