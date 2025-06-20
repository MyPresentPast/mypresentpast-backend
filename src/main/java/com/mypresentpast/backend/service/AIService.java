package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.CorrectContentRequest;
import com.mypresentpast.backend.dto.CorrectContentResponse;

/**
 * Servicio para funcionalidades de Inteligencia Artificial.
 */
public interface AIService {

    /**
     * Corrige el contenido usando IA.
     * Mejora ortografía, gramática y neutraliza contenido ofensivo.
     *
     * @param request el contenido a corregir
     * @return el contenido corregido con metadatos
     */
    CorrectContentResponse correctContent(CorrectContentRequest request);
} 