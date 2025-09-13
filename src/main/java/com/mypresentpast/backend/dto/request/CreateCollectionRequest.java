package com.mypresentpast.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de creación de colección.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectionRequest {

    @NotBlank()
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;
}
