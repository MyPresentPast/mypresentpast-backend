package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.PostVerification;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.PostVerificationRepository;
import com.mypresentpast.backend.service.PostVerificationQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostVerificationQueryServiceImpl implements PostVerificationQueryService {

    private final PostVerificationRepository postVerificationRepository;

    @Override
    public boolean isPostVerified(Post post) {
        // Regla 1: Posts de instituciones están auto-verificados
        if (post.getAuthor() != null && post.getAuthor().getRole() == UserRole.INSTITUTION) {
            return true;
        }
        
        // Regla 2: Verificar si tiene verificación externa activa
        return postVerificationRepository.existsActiveByPostId(post.getId());
    }

    @Override
    public Optional<PostVerification> getActiveVerification(Long postId) {
        return postVerificationRepository.findActiveByPostId(postId);
    }

    @Override
    public User getExternalVerifier(Long postId) {
        Optional<PostVerification> verification = getActiveVerification(postId);
        return verification.map(PostVerification::getVerifiedBy).orElse(null);
    }
}
