package com.mypresentpast.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el resumen de colección (usado en listados).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionSummaryResponse {

    private Long id;
    private String name;
    private String description;
    private Integer postCount;
}
