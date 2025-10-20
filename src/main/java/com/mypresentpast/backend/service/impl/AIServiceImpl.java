package com.mypresentpast.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypresentpast.backend.dto.request.CorrectContentRequest;
import com.mypresentpast.backend.dto.request.GeneratePostRequest;
import com.mypresentpast.backend.dto.response.CorrectContentResponse;
import com.mypresentpast.backend.dto.response.GeneratePostResponse;
import com.mypresentpast.backend.enums.Category;
import com.mypresentpast.backend.service.AIService;
import com.mypresentpast.backend.service.ai.GroqProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de IA usando OpenAI.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {

    private final GroqProvider groqProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CorrectContentResponse correctContent(CorrectContentRequest request) {
        String content = request.getContent();
        log.info("Corrigiendo contenido con IA: {} caracteres", content.length());

        try {
            // Corregir el contenido usando Groq
            String correctedContent = groqProvider.correctContent(content);

            // Verificar si hubo cambios
            boolean hasChanges = !content.equals(correctedContent);

            log.info("Corrección completada. Cambios: {}", hasChanges);

            return CorrectContentResponse.builder()
                .originalContent(content)
                .correctedContent(correctedContent)
                .hasChanges(hasChanges)
                .message("Contenido corregido por " + groqProvider.getProviderName())
                .build();

        } catch (Exception e) {
            log.error("Error al procesar contenido con IA: {}", e.getMessage(), e);

            return CorrectContentResponse.builder()
                .originalContent(content)
                .correctedContent(content)
                .hasChanges(false)
                .message("Error al procesar con IA. Contenido sin cambios.")
                .build();
        }
    }

    @Override
    public GeneratePostResponse generatePost(GeneratePostRequest request) {
        log.info("Generando publicación con IA: fecha={}, ubicación={}, contexto={} caracteres", 
            request.getDate(), request.getAddress(), request.getContext().length());

        try {
            // Generar contenido usando Groq
            String generatedJson = groqProvider.generatePostContent(
                request.getDate().toString(),
                request.getAddress(),
                request.getContext()
            );

            // Parsear la respuesta JSON
            JsonNode jsonNode = objectMapper.readTree(generatedJson);
            
            String title = jsonNode.get("title").asText();
            String content = jsonNode.get("content").asText();
            String categoryStr = jsonNode.get("category").asText();
            
            // Validar y convertir categoría
            Category category;
            try {
                category = Category.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Categoría inválida recibida: {}. Usando INFORMATION por defecto", categoryStr);
                category = Category.INFORMATION;
            }

            log.info("Publicación generada exitosamente. Título: {}, Categoría: {}", title, category);

            return GeneratePostResponse.builder()
                .title(title)
                .content(content)
                .category(category)
                .date(request.getDate())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .context(request.getContext())
                .message("Publicación generada por " + groqProvider.getProviderName())
                .success(true)
                .build();

        } catch (Exception e) {
            log.error("Error al generar publicación con IA: {}", e.getMessage(), e);

            return GeneratePostResponse.builder()
                .date(request.getDate())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .context(request.getContext())
                .message("Error al generar publicación con IA: " + e.getMessage())
                .success(false)
                .build();
        }
    }
} 