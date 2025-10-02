package com.mypresentpast.backend.controller.impl;

import com.mypresentpast.backend.controller.LikeController;
import com.mypresentpast.backend.dto.response.LikeStatusResponse;
import com.mypresentpast.backend.dto.response.LikeToggleResponse;
import com.mypresentpast.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementación del controlador de likes.
 */
@RestController
@RequiredArgsConstructor
public class LikeControllerImpl implements LikeController {

    private final LikeService likeService;

    @Override
    public ResponseEntity<LikeToggleResponse> toggleLike(Long postId) {
        LikeToggleResponse response = likeService.toggleLike(postId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LikeStatusResponse> getLikeStatus(Long postId) {
        LikeStatusResponse response = likeService.getLikeStatus(postId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Long> getTotalLikes(Long postId) {
        Long totalLikes = likeService.getTotalLikes(postId);
        return ResponseEntity.ok(totalLikes);
    }
}
