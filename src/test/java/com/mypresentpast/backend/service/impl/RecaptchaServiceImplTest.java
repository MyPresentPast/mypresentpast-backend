package com.mypresentpast.backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecaptchaServiceImplTest {

    // restTemplate es private final e inicializado inline, por lo que se captura via mockConstruction
    private RecaptchaServiceImpl recaptchaService;
    private RestTemplate restTemplate;

    private static final String TEST_SECRET_KEY = "test-secret-key";
    private static final String TEST_VERIFY_URL = "https://recaptcha.test.example.com/verify";
    private static final String TEST_TOKEN = "test-recaptcha-token";

    @BeforeEach
    void setUp() {
        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class)) {
            recaptchaService = new RecaptchaServiceImpl();
            restTemplate = mocked.constructed().get(0);
        }
        ReflectionTestUtils.setField(recaptchaService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(recaptchaService, "verifyUrl", TEST_VERIFY_URL);
    }

    // Verifica que cuando la API de Google retorna success=true, el método retorna true.
    @Test
    void verify_SuccessResponseTrue_ReturnsTrue() {
        MultiValueMap<String, String> expectedParams = new LinkedMultiValueMap<>();
        expectedParams.add("secret", TEST_SECRET_KEY);
        expectedParams.add("response", TEST_TOKEN);

        when(restTemplate.postForObject(TEST_VERIFY_URL, expectedParams, Map.class))
                .thenReturn(Map.of("success", true));

        boolean result = recaptchaService.verify(TEST_TOKEN);

        assertTrue(result);
        verify(restTemplate).postForObject(TEST_VERIFY_URL, expectedParams, Map.class);
    }

    // Verifica que cuando la API retorna success=false, el método retorna false.
    @Test
    void verify_SuccessResponseFalse_ReturnsFalse() {
        MultiValueMap<String, String> expectedParams = new LinkedMultiValueMap<>();
        expectedParams.add("secret", TEST_SECRET_KEY);
        expectedParams.add("response", TEST_TOKEN);

        when(restTemplate.postForObject(TEST_VERIFY_URL, expectedParams, Map.class))
                .thenReturn(Map.of("success", false));

        boolean result = recaptchaService.verify(TEST_TOKEN);

        assertFalse(result);
        verify(restTemplate).postForObject(TEST_VERIFY_URL, expectedParams, Map.class);
    }

    // Verifica que cuando postForObject retorna null, el método retorna false sin lanzar excepción.
    @Test
    void verify_NullResponse_ReturnsFalse() {
        MultiValueMap<String, String> expectedParams = new LinkedMultiValueMap<>();
        expectedParams.add("secret", TEST_SECRET_KEY);
        expectedParams.add("response", TEST_TOKEN);

        when(restTemplate.postForObject(TEST_VERIFY_URL, expectedParams, Map.class))
                .thenReturn(null);

        boolean result = recaptchaService.verify(TEST_TOKEN);

        assertFalse(result);
        verify(restTemplate).postForObject(TEST_VERIFY_URL, expectedParams, Map.class);
    }

    // Verifica que cuando la respuesta contiene success=null, el método retorna false.
    @Test
    void verify_NullSuccessValue_ReturnsFalse() {
        MultiValueMap<String, String> expectedParams = new LinkedMultiValueMap<>();
        expectedParams.add("secret", TEST_SECRET_KEY);
        expectedParams.add("response", TEST_TOKEN);

        Map<String, Object> response = new HashMap<>();
        response.put("success", null);
        when(restTemplate.postForObject(TEST_VERIFY_URL, expectedParams, Map.class))
                .thenReturn(response);

        boolean result = recaptchaService.verify(TEST_TOKEN);

        assertFalse(result);
        verify(restTemplate).postForObject(TEST_VERIFY_URL, expectedParams, Map.class);
    }

    // Verifica que cuando restTemplate lanza una excepción de red, el método captura el error y retorna false.
    @Test
    void verify_RestTemplateThrowsException_ReturnsFalse() {
        MultiValueMap<String, String> expectedParams = new LinkedMultiValueMap<>();
        expectedParams.add("secret", TEST_SECRET_KEY);
        expectedParams.add("response", TEST_TOKEN);

        when(restTemplate.postForObject(TEST_VERIFY_URL, expectedParams, Map.class))
                .thenThrow(new RestClientException("Connection refused"));

        boolean result = recaptchaService.verify(TEST_TOKEN);

        assertFalse(result);
        verify(restTemplate).postForObject(TEST_VERIFY_URL, expectedParams, Map.class);
    }
}
