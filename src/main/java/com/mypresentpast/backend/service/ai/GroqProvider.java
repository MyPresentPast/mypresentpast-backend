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
            "INSTRUCCIÓN CRÍTICA: DEVUELVE ÚNICAMENTE EL TEXTO CORREGIDO, SIN EXPLICACIONES, SIN COMENTARIOS, SIN METADATOS.\n\n" +
            "REGLAS ABSOLUTAS - PROHIBIDO VIOLAR:\n" +
            "1. NUNCA cambies el significado de las palabras\n" +
            "2. NUNCA inventes palabras que no existen ('nevaron', 'sábado' cuando dice 'sarpado')\n" +
            "3. NUNCA agregues palabras nuevas al texto\n" +
            "4. NUNCA cambies palabras de slang argentino (ej: 'sarpado' significa 'increíble')\n" +
            "5. Si una palabra está mal escrita pero entiendes qué quiso decir, corrígela EXACTAMENTE\n" +
            "6. Si no estás 100% seguro, NO toques la palabra\n" +
            "7. Que no se permitan malas palabras u ofensivas\n" +
            "8. JAMÁS incluyas explicaciones como 'No hay errores' o 'El texto correcto es'\n\n" +
            "EJEMPLOS DE RESPUESTA CORRECTA:\n" +
            "Usuario: 'nevo mucho ayer'\n" +
            "Tu respuesta: 'nevó mucho ayer'\n\n" +
            "Usuario: 'mi ermano es sarpado'\n" +
            "Tu respuesta: 'mi hermano es sarpado'\n\n" +
            "Usuario: 'villa maria es ermosa'\n" +
            "Tu respuesta: 'Villa María es hermosa'\n\n" +
            "EJEMPLOS DE RESPUESTA INCORRECTA:\n" +
            "❌ 'El texto correcto es: nevó mucho ayer'\n" +
            "❌ 'No hay errores de escritura obvios en el texto'\n" +
            "❌ 'La corrección sería: Villa María es hermosa'\n\n" +
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
    public String generatePostContent(String date, String location, String context) {
        if (apiKey == null || apiKey.trim().isEmpty() || "gsk_demo".equals(apiKey)) {
            throw new IllegalStateException("Groq API key no configurada. Obtén una gratis en: https://console.groq.com/keys");
        }

        String prompt = "Sos un escritor experto en narrativa personal con profundo conocimiento de historia, cultura, geografía y deportes a nivel mundial.\n\n" +
            "## CONTEXTO DE LA PLATAFORMA\n" +
            "MyPresentPast es una red social de memorias personales. Los usuarios registran momentos que ellos mismos vivieron.\n" +
            "El CONTEXTO es la memoria del usuario — es un hecho, no una hipótesis. Nunca lo pongas en duda ni lo contradigas.\n\n" +
            "## REGLA FUNDAMENTAL — LEER ANTES DE ESCRIBIR\n" +
            "JAMÁS escribas frases como:\n" +
            "- 'aunque X no ocurrió ese día'\n" +
            "- 'no hay registros de que X jugara allí'\n" +
            "- 'si bien X no estaba en esa ciudad'\n" +
            "El usuario estuvo ahí. El usuario lo vivió. Tu trabajo es narrarlo, no verificarlo.\n\n" +
            "## PASO 1 — IDENTIFICÁ EL EVENTO\n" +
            "Con la fecha y la ciudad/país (ignorá la dirección exacta, usá solo ciudad y país), determiná:\n" +
            "- ¿Hay un evento real conocido que coincida con el contexto del usuario?\n" +
            "- Si el contexto menciona un equipo, partido o deporte: buscá en tu conocimiento si hubo un partido o evento\n" +
            "  de ese equipo en esa ciudad en esa fecha o época cercana.\n" +
            "- Ejemplo guía: fecha 2018-12-09, ciudad Madrid, contexto 'fui a ver un partido de River' →\n" +
            "  La Final de la Copa Libertadores 2018 entre River Plate y Boca Juniors se jugó el 9 de diciembre de 2018\n" +
            "  en el Estadio Santiago Bernabéu de Madrid. River ganó 3-1 en tiempo extra. Usá ese dato.\n\n" +
            "## PASO 2 — ESCRIBÍ LA PUBLICACIÓN\n" +
            "Redactá en primera persona una publicación que:\n" +
            "1. Abra con una frase gancho que sitúe al lector en el momento (fecha + lugar + emoción).\n" +
            "2. Narre la experiencia del usuario enriquecida con detalles reales del evento si los identificaste.\n" +
            "3. Transmita la emoción genuina de haber estado ahí.\n" +
            "4. Tenga entre 250 y 500 caracteres. Tono cálido, personal, vívido.\n\n" +
            "## FORMATO DE RESPUESTA\n" +
            "Respondé ÚNICAMENTE con JSON válido, sin markdown, sin texto extra:\n" +
            "{\"title\": \"...\", \"content\": \"...\", \"category\": \"STORY|INFORMATION|MYTH\"}\n" +
            "Título: máximo 80 caracteres, concreto y evocador.\n" +
            "Categoría: STORY (relato personal), INFORMATION (hecho objetivo), MYTH (leyenda/tradición).\n\n" +
            "## RECUERDO\n" +
            "Fecha: " + date + "\n" +
            "Ubicación (usá solo ciudad y país): " + location + "\n" +
            "Contexto del usuario: " + context;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
            Map.of("role", "system", "content", prompt),
            Map.of("role", "user", "content", "Genera la publicación basada en la información proporcionada.")
        ));
        requestBody.put("max_tokens", 2000);
        requestBody.put("temperature", 0.8);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Enviando request a Groq para generar publicación: fecha={}, ubicación={}", date, location);

            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new RuntimeException("Respuesta vacía de Groq API");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No se recibieron opciones de Groq API");
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null) {
                throw new RuntimeException("Mensaje vacío de Groq API");
            }

            String generatedContent = (String) message.get("content");
            log.info("Groq generó publicación exitosamente");

            return generatedContent.trim();

        } catch (Exception e) {
            log.error("Error generando publicación con Groq API: {}", e.getMessage(), e);
            throw new RuntimeException("Error en API de Groq: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "Groq (Gratuito)";
    }
} 