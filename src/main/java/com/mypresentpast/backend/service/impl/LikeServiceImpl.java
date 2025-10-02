package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.LikeStatusResponse;
import com.mypresentpast.backend.dto.response.LikeToggleResponse;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.PostLike;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.PostLikeRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.LikeService;
import com.mypresentpast.backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementación del servicio de likes.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LikeServiceImpl implements LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public LikeToggleResponse toggleLike(Long postId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado con id: " + postId));

        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Optional<PostLike> existingLike = postLikeRepository.findByUserIdAndPostId(currentUserId, postId);
        
        boolean isLiked;
        String message;
        
        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            isLiked = false;
            message = "Like removido con éxito";
            log.info("Usuario {} quitó like del post {}", currentUserId, postId);
        } else {
            PostLike newLike = PostLike.builder()
                .user(user)
                .post(post)
                .build();
            postLikeRepository.save(newLike);
            isLiked = true;
            message = "Like agregado con éxito";
            log.info("Usuario {} dio like al post {}", currentUserId, postId);
        }

        Long totalLikes = postLikeRepository.countByPostId(postId);

        return LikeToggleResponse.builder()
            .message(message)
            .isLiked(isLiked)
            .totalLikes(totalLikes)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LikeStatusResponse getLikeStatus(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post no encontrado con id: " + postId);
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        boolean isLiked = postLikeRepository.existsByUserIdAndPostId(currentUserId, postId);
        Long totalLikes = postLikeRepository.countByPostId(postId);

        return LikeStatusResponse.builder()
            .isLiked(isLiked)
            .totalLikes(totalLikes)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isLikedByCurrentUser(Long postId) {
        try {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            return postLikeRepository.existsByUserIdAndPostId(currentUserId, postId);
        } catch (Exception e) {
            return false;
        }
    }
}
