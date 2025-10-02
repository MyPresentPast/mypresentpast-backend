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
 * Proveedor de IA usando Groq API (100% gratuito).
 * Groq es más rápido que OpenAI y completamente gratis.
 */
@Component
@Slf4j
public class GroqProvider implements AIProvider {

    @Value("${ai.groq.api-key:gsk_demo}")
    private String apiKey;

    @Value("${ai.groq.api-url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.groq.model:llama-3.1-8b-instant}")
    private String model;

    @Value("${ai.groq.max-tokens:1000}")
    private int maxTokens;

    @Value("${ai.groq.temperature:0.3}")
    private double temperature;

    private final RestTemplate restTemplate;

    public GroqProvider() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String correctContent(String content) {
        if (apiKey == null || apiKey.trim().isEmpty() || "gsk_demo".equals(apiKey)) {
            throw new IllegalStateException("Groq API key no configurada. Obtén una gratis en: https://console.groq.com/keys");
        }

        String prompt = "Eres un CORRECTOR ORTOGRÁFICO BÁSICO. SOLO corrige errores de escritura obvios.\n\n" +
            "REGLAS ABSOLUTAS - PROHIBIDO VIOLAR:\n" +
            "1. NUNCA cambies el significado de las palabras\n" +
            "2. NUNCA inventes palabras que no existen ('nevaron', 'sábado' cuando dice 'sarpado')\n" +
            "3. NUNCA agregues palabras nuevas al texto\n" +
            "4. NUNCA cambies palabras de slang argentino (ej: 'sarpado' significa 'increíble')\n" +
            "5. Si una palabra está mal escrita pero entiendes qué quiso decir, corrígela EXACTAMENTE\n" +
            "6. Si no estás 100% seguro, NO toques la palabra\n\n" +
            "7. Que no se permitan malas palabras u ofensivas.\n\n" +
            "EJEMPLOS CORRECTOS:\n" +
            "- 'nevo' → 'nevó' (falta tilde)\n" +
            "- 'ermano' → 'hermano' (falta h)\n" +
            "- 'añoz' → 'años' (z por s)\n" +
            "- 'villa maria' → 'Villa María' (mayúsculas y tilde)\n" +
            "- 'sarpado' → 'sarpado' (NO tocar, es slang argentino correcto)\n" +
            "- 'increible' → 'increíble' (falta tilde)\n\n" +
            "EJEMPLOS PROHIBIDOS:\n" +
            "- 'nevo' → 'nevaron' ❌ (cambió número y tiempo)\n" +
            "- 'sarpado' → 'sábado' ❌ (cambió completamente el significado)\n\n" +
            "TEXTO A CORREGIR:";

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
            log.info("Enviando request a Groq para corregir contenido: {} caracteres", content.length());

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
            log.info("Groq respondió exitosamente");

            return correctedContent.trim();

        } catch (Exception e) {
            log.error("Error llamando a Groq API: {}", e.getMessage(), e);
            throw new RuntimeException("Error en API de Groq: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "Groq (Gratuito)";
    }
} 