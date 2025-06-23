package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.CorrectContentRequest;
import com.mypresentpast.backend.dto.response.CorrectContentResponse;
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
} 