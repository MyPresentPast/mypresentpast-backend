package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.request.CorrectContentRequest;
import com.mypresentpast.backend.dto.request.GeneratePostRequest;
import com.mypresentpast.backend.dto.response.CorrectContentResponse;
import com.mypresentpast.backend.dto.response.GeneratePostResponse;

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

    /**
     * Genera una publicación completa usando IA.
     * Basándose en fecha, ubicación y contexto, genera título, contenido y categoría.
     *
     * @param request los datos base para generar la publicación
     * @return la publicación generada con metadatos
     */
    GeneratePostResponse generatePost(GeneratePostRequest request);
} 