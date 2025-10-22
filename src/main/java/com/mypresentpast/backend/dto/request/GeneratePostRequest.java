package com.mypresentpast.backend.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar generación de publicación con IA.
 * El usuario proporciona fecha, ubicación y contexto, y la IA genera título, contenido y categoría.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePostRequest {

    @NotNull()
    private LocalDate date;

    @NotNull()
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @NotNull()
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    @NotBlank()
    @Size(min = 2, max = 255)
    private String address;

    @NotBlank()
    @Size(min = 10, max = 1000)
    private String context;

    @NotNull()
    private Long authorId;
}
