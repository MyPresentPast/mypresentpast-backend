package com.mypresentpast.backend.dto.request;

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
} 