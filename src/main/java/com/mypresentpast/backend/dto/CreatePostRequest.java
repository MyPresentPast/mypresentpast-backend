package com.mypresentpast.backend.dto;

import com.mypresentpast.backend.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new post from frontend form.
 * Frontend envía coordenadas exactas del clic en el mapa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {

    @NotBlank(message = "El nombre es requerido")
    private String title;

    @NotNull(message = "La latitud es requerida")
    private Double latitude;

    @NotNull(message = "La longitud es requerida")
    private Double longitude;

    @NotBlank(message = "La dirección es requerida")
    private String address;

    @NotBlank(message = "El contenido es requerido")
    private String content;

    @NotNull(message = "La fecha es requerida")
    private LocalDate date;

    @NotNull(message = "La categoría es requerida")
    private Category category;

    @NotNull(message = "El autor es requerido")
    private Long authorId;

    @Builder.Default
    private Boolean isByIA = false;
} 