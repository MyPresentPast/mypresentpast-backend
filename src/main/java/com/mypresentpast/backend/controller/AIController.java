package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.CorrectContentRequest;
import com.mypresentpast.backend.dto.CorrectContentResponse;
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
} 