package com.mypresentpast.backend.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mypresentpast.backend.service.ai.GroqProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AIServiceImplTest {

    @Mock
    private GroqProvider groqProvider;

    @InjectMocks
    private AIServiceImpl aiService;

    @Test
    void groqProvider_CanBeMocked() {
        // Given
        String input = "test content";
        String expectedOutput = "corrected content";

        when(groqProvider.correctContent(input)).thenReturn(expectedOutput);
        when(groqProvider.getProviderName()).thenReturn("Groq");

        // When
        String result = groqProvider.correctContent(input);
        String name = groqProvider.getProviderName();

        // Then
        assertEquals(expectedOutput, result);
        assertEquals("Groq", name);
        verify(groqProvider).correctContent(input);
        verify(groqProvider).getProviderName();
    }

    @Test
    void groqProvider_HandleException() {
        // Given
        when(groqProvider.correctContent(anyString())).thenThrow(new RuntimeException("API Error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> groqProvider.correctContent("test"));
        verify(groqProvider).correctContent("test");
    }
} 