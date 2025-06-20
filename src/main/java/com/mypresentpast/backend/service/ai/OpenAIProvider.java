package com.mypresentpast.backend.service.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Proveedor de IA usando OpenAI GPT.
 */
@Component
@Slf4j
public class OpenAIProvider implements AIProvider {

    @Value("${ai.openai.api-key:}")
    private String apiKey;

    @Value("${ai.openai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.openai.model:gpt-3.5-turbo}")
    private String model;

    @Value("${ai.openai.max-tokens:1000}")
    private int maxTokens;

    @Value("${ai.openai.temperature:0.3}")
    private double temperature;

    private final RestTemplate restTemplate;

    public OpenAIProvider() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String correctContent(String content) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("OpenAI API key no configurada. Verifica application.properties");
        }

        String prompt = "Eres un corrector de texto especializado en contenido histórico. Tu tarea es:\n" +
            "1. Corregir errores ortográficos y gramaticales\n" +
            "2. Neutralizar contenido ofensivo, discriminatorio o inapropiado\n" +
            "3. Mantener el contexto histórico y educativo\n" +
            "4. Devolver solo el texto corregido, sin explicaciones adicionales\n\n" +
            "Texto a corregir:";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
            Map.of("role", "system", "content", prompt),
            Map.of("role", "user", "content", content)
        ));
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", temperature);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Enviando request a OpenAI para corregir contenido: {} caracteres", content.length());

            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            String correctedContent = (String) message.get("content");
            log.info("OpenAI respondió exitosamente");

            return correctedContent.trim();

        } catch (Exception e) {
            log.error("Error llamando a OpenAI API: {}", e.getMessage(), e);
            throw new RuntimeException("Error en API de OpenAI: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "OpenAI";
    }
} 