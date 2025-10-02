package com.mypresentpast.backend.controller.impl;

import com.mypresentpast.backend.controller.CollectionController;
import com.mypresentpast.backend.dto.request.CreateCollectionAndSavePostRequest;
import com.mypresentpast.backend.dto.request.CreateCollectionRequest;
import com.mypresentpast.backend.dto.request.UpdateCollectionRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.CollectionSummaryResponse;
import com.mypresentpast.backend.dto.response.PostCollectionStatusResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import com.mypresentpast.backend.service.CollectionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementación del controlador REST para operaciones relacionadas con Collection.
 */
@RestController
@RequiredArgsConstructor
public class CollectionControllerImpl implements CollectionController {

    private final CollectionService collectionService;

    @Override
    public ResponseEntity<List<CollectionSummaryResponse>> getMyCollections() {
        List<CollectionSummaryResponse> collections = collectionService.getMyCollections();
        return ResponseEntity.ok(collections);
    }

    @Override
    public ResponseEntity<ApiResponse> createCollection(CreateCollectionRequest request) {
        ApiResponse response = collectionService.createCollection(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> updateCollection(Long id, UpdateCollectionRequest request) {
        ApiResponse response = collectionService.updateCollection(id, request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteCollection(Long id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<PostResponse>> getCollectionPosts(Long id) {
        List<PostResponse> posts = collectionService.getCollectionPosts(id);
        return ResponseEntity.ok(posts);
    }

    @Override
    public ResponseEntity<PostCollectionStatusResponse> getPostCollectionStatus(Long postId) {
        PostCollectionStatusResponse status = collectionService.getPostCollectionStatus(postId);
        return ResponseEntity.ok(status);
    }

    @Override
    public ResponseEntity<ApiResponse> addPostToCollection(Long collectionId, Long postId) {
        ApiResponse response = collectionService.addPostToCollection(collectionId, postId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> removePostFromCollection(Long collectionId, Long postId) {
        ApiResponse response = collectionService.removePostFromCollection(collectionId, postId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> createCollectionAndSavePost(CreateCollectionAndSavePostRequest request) {
        ApiResponse response = collectionService.createCollectionAndSavePost(request);
        return ResponseEntity.ok(response);
    }
}
