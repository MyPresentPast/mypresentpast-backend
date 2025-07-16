package com.mypresentpast.backend.controller.impl;

import com.mypresentpast.backend.controller.CategoryController;
import com.mypresentpast.backend.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementación del controlador de categorías.
 */
@RestController
@RequiredArgsConstructor
public class CategoryControllerImpl implements CategoryController {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = categoryService.getCategories();
        return ResponseEntity.ok(categories);
    }
} 