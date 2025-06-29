package com.mypresentpast.backend.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO simplificado para el endpoint del mapa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapResponse {

    /**
     * Lista completa de posts en el área solicitada
     */
    private List<PostResponse> posts;
} 