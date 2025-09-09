package com.mypresentpast.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de crear colección y guardar post en un solo paso.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectionAndSavePostRequest {

    @NotBlank()
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull()
    private Long postId;
}
