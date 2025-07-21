package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.enums.Category;
import com.mypresentpast.backend.service.CategoryService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de categorías.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Override
    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return Arrays.stream(Category.values())
            .map(Category::name)
            .collect(Collectors.toList());
    }
} 