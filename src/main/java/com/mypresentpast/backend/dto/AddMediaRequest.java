package com.mypresentpast.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para agregar imágenes a una publicación existente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMediaRequest {

    @NotEmpty(message = "Debe proporcionar al menos una imagen")
    @Size(max = 5, message = "Máximo 5 imágenes permitidas")
    private List<String> imageUrls;
} 