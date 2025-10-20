package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.request.CorrectContentRequest;
import com.mypresentpast.backend.dto.request.GeneratePostRequest;
import com.mypresentpast.backend.dto.response.CorrectContentResponse;
import com.mypresentpast.backend.dto.response.GeneratePostResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para funcionalidades de Inteligencia Artificial.
 */
@RequestMapping("/ai")
public interface AIController {

    /**
     * Corrige contenido usando IA.
     * Mejora ortografía, gramática y neutraliza contenido ofensivo.
     *
     * @param request el contenido a corregir
     * @return el contenido corregido con metadatos
     */
    @PostMapping("/correct-content")
    ResponseEntity<CorrectContentResponse> correctContent(@Valid @RequestBody CorrectContentRequest request);

    /**
     * Genera una publicación completa usando IA.
     * Basándose en fecha, ubicación y contexto, genera título, contenido y categoría.
     *
     * @param request los datos base para generar la publicación
     * @return la publicación generada con metadatos
     */
    @PostMapping("/generate-post")
    ResponseEntity<GeneratePostResponse> generatePost(@Valid @RequestBody GeneratePostRequest request);
} 