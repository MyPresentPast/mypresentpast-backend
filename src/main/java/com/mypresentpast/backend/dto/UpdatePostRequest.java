package com.mypresentpast.backend.dto;

import com.mypresentpast.backend.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing post.
 * Incluye información sobre qué imágenes mantener y cuáles eliminar.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {

    @NotBlank(message = "El título es requerido")
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

    /**
     * IDs de las imágenes existentes que se deben mantener.
     * Las que no estén en esta lista serán eliminadas.
     */
    private List<Long> keepImageIds;
} 