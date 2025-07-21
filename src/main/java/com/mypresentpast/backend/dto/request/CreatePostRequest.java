package com.mypresentpast.backend.dto.request;

import com.mypresentpast.backend.enums.Category;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Pattern(regexp = "^(?=.*[a-zA-ZÀ-ÿñÑ]).*$", message = "El título debe contener al menos una letra")
    @Size(min = 3, max = 500, message = "El título debe tener entre 3 y 500 caracteres")
    private String title;

    @NotNull()
    //@DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    //@DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Double latitude;

    @NotNull()
    //@DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    //@DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Double longitude;

    @NotBlank()
    @Size(min = 2, max = 255, message = "La dirección debe tener entre 2 y 255 caracteres")
    private String address;

    @NotBlank()
    @Pattern(regexp = "^(?=.*[a-zA-ZÀ-ÿñÑ]).*$", message = "El contenido debe contener al menos una letra")
    @Size(min = 10, max = 10000, message = "El contenido debe tener entre 10 y 10000 caracteres")
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