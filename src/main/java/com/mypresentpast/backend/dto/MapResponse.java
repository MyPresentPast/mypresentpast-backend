package com.mypresentpast.backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO simplificado para el endpoint del mapa.
 * Solo devuelve la lista de posts. El frontend maneja todo lo demás.
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