package com.mypresentpast.backend.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypresentpast.backend.controller.PostController;
import com.mypresentpast.backend.dto.request.CreatePostRequest;
import com.mypresentpast.backend.dto.request.UpdatePostRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.MapResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import com.mypresentpast.backend.service.PostService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementación del controlador de Post.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostControllerImpl implements PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<ApiResponse> createPost(String data, List<MultipartFile> images) throws JsonProcessingException {
        CreatePostRequest request = objectMapper.readValue(data, CreatePostRequest.class);
        ApiResponse response = postService.createPost(request, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<PostResponse> getPostById(Long id) {
        PostResponse response = postService.getPostById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MapResponse> getMapData(
        double latMin, double latMax, double lonMin, double lonMax,
        String category, LocalDate date, Boolean isVerified, Boolean isByIA) {

        if (date != null && date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden consultar fechas futuras");
        }

        MapResponse response = postService.getMapData(
            latMin, latMax, lonMin, lonMax,
            category, date, isVerified, isByIA
        );
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PostResponse> getRandomPost() {
        PostResponse response = postService.getRandomPost();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> updatePost(Long id, String data, List<MultipartFile> newImages) throws JsonProcessingException {
        UpdatePostRequest request = objectMapper.readValue(data, UpdatePostRequest.class);
        ApiResponse response = postService.updatePost(id, request, newImages);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> deletePost(Long id) {
        ApiResponse response = postService.deletePost(id);
        return ResponseEntity.ok(response);
    }
}