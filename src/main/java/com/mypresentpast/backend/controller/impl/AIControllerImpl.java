package com.mypresentpast.backend.controller.impl;

import com.mypresentpast.backend.controller.AIController;
import com.mypresentpast.backend.dto.request.CorrectContentRequest;
import com.mypresentpast.backend.dto.request.GeneratePostRequest;
import com.mypresentpast.backend.dto.response.CorrectContentResponse;
import com.mypresentpast.backend.dto.response.GeneratePostResponse;
import com.mypresentpast.backend.service.AIService;
import com.mypresentpast.backend.service.ai.OpenAIProvider;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementación del controlador de IA.
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class AIControllerImpl implements AIController {
    
    private final AIService aiService;
    private final OpenAIProvider openAIProvider;
    
    @Override
    public ResponseEntity<CorrectContentResponse> correctContent(CorrectContentRequest request) {
        log.info("Solicitud de corrección de contenido recibida");
        CorrectContentResponse response = aiService.correctContent(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GeneratePostResponse> generatePost(GeneratePostRequest request) {
        log.info("Solicitud de generación de publicación recibida: fecha={}, ubicación={}", 
            request.getDate(), request.getAddress());
        GeneratePostResponse response = aiService.generatePost(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/debug/openai")
    public ResponseEntity<Map<String, Object>> debugOpenAI() {
        Map<String, Object> debug = new HashMap<>();
        try {
            log.info("Testing OpenAI connection...");
            String result = openAIProvider.correctContent("test");
            debug.put("status", "success");
            debug.put("provider", openAIProvider.getProviderName());
            debug.put("result", result);
        } catch (Exception e) {
            log.error("OpenAI test failed: {}", e.getMessage(), e);
            debug.put("status", "error");
            debug.put("error", e.getMessage());
            debug.put("provider", openAIProvider.getProviderName());
        }
        return ResponseEntity.ok(debug);
    }
} 