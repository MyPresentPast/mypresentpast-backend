package com.mypresentpast.backend.dto.request;

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

    @NotBlank()
    private String title;

    @NotNull()
    private Double latitude;

    @NotNull()
    private Double longitude;

    @NotBlank()
    private String address;

    @NotBlank()
    private String content;

    @NotNull()
    private LocalDate date;

    @NotNull()
    private Category category;

    @NotNull()
    private Long authorId;

    @Builder.Default
    private Boolean isByIA = false;

    /**
     * IDs de las imágenes existentes que se deben mantener.
     * Las que no estén en esta lista serán eliminadas.
     */
    private List<Long> keepImageIds;
} 