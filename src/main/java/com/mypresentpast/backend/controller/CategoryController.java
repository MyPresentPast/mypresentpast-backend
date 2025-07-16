package com.mypresentpast.backend.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para operaciones relacionadas con categorías.
 */
@RequestMapping("/categories")
public interface CategoryController {

    /**
     * Obtener todas las categorías disponibles.
     *
     * @return lista de categorías disponibles
     */
    @GetMapping
    ResponseEntity<List<String>> getCategories();
} 