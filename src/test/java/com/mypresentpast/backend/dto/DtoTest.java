package com.mypresentpast.backend.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.mypresentpast.backend.dto.request.CreatePostRequest;
import com.mypresentpast.backend.dto.request.CorrectContentRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.CorrectContentResponse;
import com.mypresentpast.backend.dto.response.MapResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import com.mypresentpast.backend.enums.Category;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Tests simples para DTOs usando builders de Lombok
 */
class DtoTest {

    @Test
    void createPostRequest_CanBeBuilt() {
        // When
        CreatePostRequest request = CreatePostRequest.builder()
            .title("Test Title")
            .content("Test Content")
            .date(LocalDate.now())
            .category(Category.STORY)
            .authorId(1L)
            .latitude(-34.6118)
            .longitude(-58.3960)
            .address("Test Address")
            .isByIA(false)
            .build();

        // Then
        assertNotNull(request);
    }

    @Test
    void apiResponse_CanBeBuilt() {
        // When
        ApiResponse response = ApiResponse.builder()
            .message("Test message")
            .build();

        // Then
        assertNotNull(response);
    }

    @Test
    void correctContentRequest_CanBeBuilt() {
        // When
        CorrectContentRequest request = CorrectContentRequest.builder()
            .content("Test content")
            .build();

        // Then
        assertNotNull(request);
    }

    @Test
    void correctContentResponse_CanBeBuilt() {
        // When
        CorrectContentResponse response = CorrectContentResponse.builder()
            .originalContent("Original")
            .correctedContent("Corrected")
            .hasChanges(true)
            .message("Test message")
            .build();

        // Then
        assertNotNull(response);
    }

    @Test
    void postResponse_CanBeBuilt() {
        // When
        PostResponse response = PostResponse.builder()
            .id(1L)
            .title("Test Title")
            .content("Test Content")
            .build();

        // Then
        assertNotNull(response);
    }

    @Test
    void mapResponse_CanBeBuilt() {
        // When
        MapResponse response = MapResponse.builder()
            .posts(java.util.Arrays.asList())
            .build();

        // Then
        assertNotNull(response);
    }
} 