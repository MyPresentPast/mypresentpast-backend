package com.mypresentpast.backend.service.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// RestTemplate se crea en el constructor con new RestTemplate(), por lo que se inyecta
// el mock via reflexión. Los campos @Value también se inyectan por reflexión en setUp().
@ExtendWith(MockitoExtension.class)
class OpenAIProviderTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private OpenAIProvider openAIProvider;

    private static final String VALID_API_KEY = "sk-valid-test-api-key";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @BeforeEach
    void setUp() throws Exception {
        openAIProvider = new OpenAIProvider();
        setField("apiKey", VALID_API_KEY);
        setField("apiUrl", API_URL);
        setField("model", "gpt-3.5-turbo");
        setField("maxTokens", 1000);
        setField("temperature", 0.3);

        Field restTemplateField = OpenAIProvider.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(openAIProvider, mockRestTemplate);
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = OpenAIProvider.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(openAIProvider, value);
    }

    // ── correctContent ─────────────────────────────────────────────────────

    // Una clave vacía debe lanzar IllegalStateException sin llamar a la API.
    @Test
    void correctContent_EmptyApiKey_ThrowsIllegalStateException() throws Exception {
        setField("apiKey", "");

        assertThrows(IllegalStateException.class, () -> openAIProvider.correctContent("texto"));

        verifyNoInteractions(mockRestTemplate);
    }

    // Una clave null debe lanzar IllegalStateException sin llamar a la API.
    @Test
    void correctContent_NullApiKey_ThrowsIllegalStateException() throws Exception {
        setField("apiKey", null);

        assertThrows(IllegalStateException.class, () -> openAIProvider.correctContent("texto"));

        verifyNoInteractions(mockRestTemplate);
    }

    // Con clave válida y respuesta exitosa, retorna el contenido corregido sin espacios extra.
    @Test
    void correctContent_ValidApiKey_ReturnsCorrectContent() {
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseWithContent("  texto corregido  "));

        String result = openAIProvider.correctContent("texto a correguir");

        assertEquals("texto corregido", result);
        verify(mockRestTemplate).exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class));
    }

    // Cuando la API lanza una excepción de red, debe envolverse en RuntimeException con mensaje descriptivo.
    @Test
    void correctContent_RestTemplateThrowsException_ThrowsRuntimeException() {
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Timeout de red"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> openAIProvider.correctContent("texto"));

        assertTrue(ex.getMessage().contains("Error en API de OpenAI"));
    }

    // ── generatePostContent ────────────────────────────────────────────────

    // Una clave vacía debe lanzar IllegalStateException sin generar contenido.
    @Test
    void generatePostContent_EmptyApiKey_ThrowsIllegalStateException() throws Exception {
        setField("apiKey", "");

        assertThrows(IllegalStateException.class,
                () -> openAIProvider.generatePostContent("2024-01-15", "Buenos Aires, Argentina", "contexto"));

        verifyNoInteractions(mockRestTemplate);
    }

    // Con parámetros válidos y respuesta exitosa, retorna el contenido generado sin espacios extra.
    @Test
    void generatePostContent_ValidRequest_ReturnsGeneratedContent() {
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseWithContent("  {\"title\":\"Título\",\"content\":\"Contenido\"}  "));

        String result = openAIProvider.generatePostContent("2024-01-15", "Buenos Aires, Argentina", "fui al estadio");

        assertEquals("{\"title\":\"Título\",\"content\":\"Contenido\"}", result);
        verify(mockRestTemplate).exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class));
    }

    // Un cuerpo de respuesta nulo debe lanzar RuntimeException envuelta.
    @Test
    void generatePostContent_NullResponseBody_ThrowsRuntimeException() {
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> openAIProvider.generatePostContent("2024-01-15", "Buenos Aires", "contexto"));

        assertTrue(ex.getMessage().contains("Error en API de OpenAI"));
    }

    // Una lista de choices vacía debe lanzar RuntimeException envuelta.
    @Test
    void generatePostContent_EmptyChoices_ThrowsRuntimeException() {
        Map<String, Object> body = Map.of("choices", List.of());
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> openAIProvider.generatePostContent("2024-01-15", "Buenos Aires", "contexto"));

        assertTrue(ex.getMessage().contains("Error en API de OpenAI"));
    }

    // Un message nulo en el primer choice debe lanzar RuntimeException envuelta.
    @Test
    void generatePostContent_NullMessage_ThrowsRuntimeException() {
        Map<String, Object> choiceWithNullMessage = new HashMap<>();
        choiceWithNullMessage.put("message", null);
        Map<String, Object> body = Map.of("choices", List.of(choiceWithNullMessage));
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> openAIProvider.generatePostContent("2024-01-15", "Buenos Aires", "contexto"));

        assertTrue(ex.getMessage().contains("Error en API de OpenAI"));
    }

    // Cuando la API lanza excepción al generar, debe envolverse en RuntimeException.
    @Test
    void generatePostContent_RestTemplateThrowsException_ThrowsRuntimeException() {
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> openAIProvider.generatePostContent("2024-01-15", "Buenos Aires", "contexto"));

        assertTrue(ex.getMessage().contains("Error en API de OpenAI"));
    }

    // ── getProviderName ────────────────────────────────────────────────────

    // El nombre del proveedor debe identificar correctamente a OpenAI.
    @Test
    void getProviderName_ReturnsOpenAIProviderName() {
        assertEquals("OpenAI", openAIProvider.getProviderName());
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private ResponseEntity<Map> responseWithContent(String content) {
        Map<String, Object> message = Map.of("content", content);
        Map<String, Object> choice = Map.of("message", message);
        Map<String, Object> body = Map.of("choices", List.of(choice));
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
