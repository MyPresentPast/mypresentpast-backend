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
    public String generatePostContent(String date, String location, String context) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("OpenAI API key no configurada. Verifica application.properties");
        }

        String prompt = "Eres un experto en historia y narrativa que crea publicaciones para una plataforma llamada MyPresentPast.\n\n" +
            "INSTRUCCIONES ESPECÍFICAS:\n" +
            "1. Genera una publicación histórica basada en la información proporcionada\n" +
            "2. El título debe ser atractivo y descriptivo (máximo 100 caracteres)\n" +
            "3. El contenido debe ser informativo, bien estructurado y entre 200-800 caracteres\n" +
            "4. Elige UNA categoría apropiada: STORY (historias/relatos), INFORMATION (datos/hechos), o MYTH (leyendas/mitos)\n" +
            "5. Usa un tono educativo pero accesible\n" +
            "6. Si no hay información histórica específica, crea contenido relevante para la época y lugar\n\n" +
            "MANEJO DE FECHAS - MUY IMPORTANTE:\n" +
            "- PRIMERO verifica si la fecha proporcionada coincide exactamente con algún evento histórico del contexto\n" +
            "- Si la fecha COINCIDE exactamente: menciona que el evento ocurrió ese día específico\n" +
            "- Si la fecha NO coincide: usa frases como 'A X días de...', 'En la época de...', 'Durante este período...'\n" +
            "- NUNCA inventes que un evento ocurrió en una fecha incorrecta\n" +
            "- Ejemplos:\n" +
            "  * Si el contexto es 'River vs Boca' y la fecha es '2018-12-09': 'El 9 de diciembre de 2018 se jugó la final...'\n" +
            "  * Si el contexto es 'River vs Boca' y la fecha es '2018-12-15': 'A 6 días de la histórica final...'\n\n" +
            "FORMATO DE RESPUESTA (JSON válido):\n" +
            "{\n" +
            "  \"title\": \"Título de la publicación\",\n" +
            "  \"content\": \"Contenido detallado de la publicación\",\n" +
            "  \"category\": \"STORY|INFORMATION|MYTH\"\n" +
            "}\n\n" +
            "INFORMACIÓN PROPORCIONADA:\n" +
            "- Fecha: " + date + "\n" +
            "- Ubicación: " + location + "\n" +
            "- Contexto: " + context;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
            Map.of("role", "system", "content", prompt),
            Map.of("role", "user", "content", "Genera la publicación basada en la información proporcionada.")
        ));
        requestBody.put("max_tokens", 1500);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Enviando request a OpenAI para generar publicación: fecha={}, ubicación={}", date, location);

            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new RuntimeException("Respuesta vacía de OpenAI API");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No se recibieron opciones de OpenAI API");
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null) {
                throw new RuntimeException("Mensaje vacío de OpenAI API");
            }

            String generatedContent = (String) message.get("content");
            log.info("OpenAI generó publicación exitosamente");

            return generatedContent.trim();

        } catch (Exception e) {
            log.error("Error generando publicación con OpenAI API: {}", e.getMessage(), e);
            throw new RuntimeException("Error en API de OpenAI: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "OpenAI";
    }
} 