package com.mypresentpast.backend.service;

import java.util.List;

/**
 * Servicio para operaciones relacionadas con categorías.
 */
public interface CategoryService {

    /**
     * Obtener todas las categorías disponibles.
     *
     * @return lista de nombres de categorías
     */
    List<String> getCategories();
} 