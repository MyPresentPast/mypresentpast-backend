package com.mypresentpast.backend.service.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
class GroqProviderTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private GroqProvider groqProvider;

    private static final String VALID_API_KEY = "gsk_valid_test_key_not_demo";
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    @BeforeEach
    void setUp() throws Exception {
        groqProvider = new GroqProvider();
        setField("apiKey", VALID_API_KEY);
        setField("apiUrl", API_URL);
        setField("model", "llama-3.1-8b-instant");
        setField("maxTokens", 1000);
        setField("temperature", 0.3);

        Field restTemplateField = GroqProvider.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(groqProvider, mockRestTemplate);
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = GroqProvider.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(groqProvider, value);
    }

    // ── correctContent ─────────────────────────────────────────────────────

    // Una clave inválida (null, vacía o demo) debe lanzar IllegalStateException sin llamar a la API.
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "gsk_demo"})
    void correctContent_InvalidApiKey_ThrowsIllegalStateException(String invalidKey) throws Exception {
        setField("apiKey", invalidKey);

        assertThrows(IllegalStateException.class, () -> groqProvider.correctContent("texto"));

        verifyNoInteractions(mockRestTemplate);
    }

    // Con clave válida y respuesta exitosa, retorna el contenido corregido sin espacios extra.
    @Test
    void correctContent_ValidApiKey_ReturnsCorrectContent() {
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseWithContent("  texto corregido  "));

        String result = groqProvider.correctContent("texto a correguir");

        assertEquals("texto corregido", result);
        verify(mockRestTemplate).exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class));
    }

    // Cuando la API lanza una excepción de red, debe envolverse en RuntimeException con mensaje descriptivo.
    @Test
    void correctContent_RestTemplateThrowsException_ThrowsRuntimeException() {
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Timeout de red"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> groqProvider.correctContent("texto"));

        assertTrue(ex.getMessage().contains("Error en API de Groq"));
    }

    // ── generatePostContent ────────────────────────────────────────────────

    // La clave "gsk_demo" debe lanzar IllegalStateException sin generar contenido.
    @Test
    void generatePostContent_DemoApiKey_ThrowsIllegalStateException() throws Exception {
        setField("apiKey", "gsk_demo");

        assertThrows(IllegalStateException.class,
                () -> groqProvider.generatePostContent("2024-01-15", "Buenos Aires, Argentina", "contexto"));

        verifyNoInteractions(mockRestTemplate);
    }

    // Con parámetros válidos y respuesta exitosa, retorna el contenido generado sin espacios extra.
    @Test
    void generatePostContent_ValidRequest_ReturnsGeneratedContent() {
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseWithContent("  {\"title\":\"Título\",\"content\":\"Contenido\"}  "));

        String result = groqProvider.generatePostContent("2024-01-15", "Buenos Aires, Argentina", "fui al estadio");

        assertEquals("{\"title\":\"Título\",\"content\":\"Contenido\"}", result);
        verify(mockRestTemplate).exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class));
    }

    // Un cuerpo de respuesta nulo debe lanzar RuntimeException envuelta.
    @Test
    void generatePostContent_NullResponseBody_ThrowsRuntimeException() {
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> groqProvider.generatePostContent("2024-01-15", "Buenos Aires", "contexto"));

        assertTrue(ex.getMessage().contains("Error en API de Groq"));
    }

    // Una lista de choices vacía debe lanzar RuntimeException envuelta.
    @Test
    void generatePostContent_EmptyChoices_ThrowsRuntimeException() {
        Map<String, Object> body = Map.of("choices", List.of());
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> groqProvider.generatePostContent("2024-01-15", "Buenos Aires", "contexto"));

        assertTrue(ex.getMessage().contains("Error en API de Groq"));
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
                () -> groqProvider.generatePostContent("2024-01-15", "Buenos Aires", "contexto"));

        assertTrue(ex.getMessage().contains("Error en API de Groq"));
    }

    // Cuando la API lanza excepción al generar, debe envolverse en RuntimeException.
    @Test
    void generatePostContent_RestTemplateThrowsException_ThrowsRuntimeException() {
        when(mockRestTemplate.exchange(eq(API_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> groqProvider.generatePostContent("2024-01-15", "Buenos Aires", "contexto"));

        assertTrue(ex.getMessage().contains("Error en API de Groq"));
    }

    // ── getProviderName ────────────────────────────────────────────────────

    // El nombre del proveedor debe identificar a Groq como gratuito.
    @Test
    void getProviderName_ReturnsGroqProviderName() {
        assertEquals("Groq (Gratuito)", groqProvider.getProviderName());
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
