package com.mypresentpast.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de actualización de colección.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCollectionRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;
}
