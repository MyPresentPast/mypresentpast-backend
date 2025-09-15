package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.exception.UnauthorizedException;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.PostVerification;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.PostVerificationRepository;
import com.mypresentpast.backend.service.PostVerificationService;
import com.mypresentpast.backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostVerificationServiceImpl implements PostVerificationService {

    private final PostRepository postRepository;
    private final PostVerificationRepository postVerificationRepository;

    @Override
    public ApiResponse verifyPost(Long postId) {
        log.info("Iniciando verificación de post con ID: {}", postId);
        
        // Obtener usuario autenticado
        User currentUser = SecurityUtils.getCurrentUser();
        log.info("Usuario actual: ID={}, Email={}, Rol={}", 
            currentUser.getId(), currentUser.getEmail(), currentUser.getRole());
        
        // Validar que el usuario sea una institución
        if (currentUser.getRole() != UserRole.INSTITUTION) {
            log.warn("Usuario con rol {} intentó verificar post. Solo INSTITUTION puede verificar", 
                currentUser.getRole());
            throw new UnauthorizedException("Solo las instituciones pueden verificar posts");
        }
        
        // Buscar el post con relaciones cargadas
        Post post = postRepository.findByIdWithRelations(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado"));
        
        // Validar que el post esté activo
        if (post.getStatus() != PostStatus.ACTIVE) {
            throw new BadRequestException("Solo se pueden verificar posts activos");
        }
        
        // Validar que no sea su propio post (posts de institución ya están auto-verificados)
        if (post.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("No puedes verificar tus propios posts, ya están auto-verificados");
        }
        
        // Validar que no exista una verificación activa
        if (postVerificationRepository.existsActiveByPostId(postId)) {
            throw new BadRequestException("Este post ya está verificado por otra institución");
        }
        
        // Crear nueva verificación
        PostVerification verification = PostVerification.builder()
            .post(post)
            .verifiedBy(currentUser)
            .verifiedAt(LocalDateTime.now())
            .isActive(true)
            .build();
        
        PostVerification savedVerification = postVerificationRepository.save(verification);
        log.info("Verificación guardada con ID: {}", savedVerification.getId());
        
        log.info("Post {} verificado exitosamente por usuario {}", postId, currentUser.getId());
        
        return ApiResponse.builder()
            .message("Post verificado exitosamente")
            .build();
    }

    @Override
    public ApiResponse unverifyPost(Long postId) {
        log.info("Iniciando desverificación de post con ID: {}", postId);
        
        // Obtener usuario autenticado
        User currentUser = SecurityUtils.getCurrentUser();
        
        // Validar que el usuario sea una institución
        if (currentUser.getRole() != UserRole.INSTITUTION) {
            throw new UnauthorizedException("Solo las instituciones pueden desverificar posts");
        }
        
        // Buscar la verificación activa del post realizada por el usuario actual
        Optional<PostVerification> verificationOpt = postVerificationRepository
            .findActiveByPostIdAndVerifiedBy(postId, currentUser.getId());
        
        if (verificationOpt.isEmpty()) {
            throw new BadRequestException("No puedes desverificar este post. Solo puedes desverificar posts que tú hayas verificado");
        }
        
        // Desactivar la verificación
        PostVerification verification = verificationOpt.get();
        verification.setIsActive(false);
        postVerificationRepository.save(verification);
        
        log.info("Post {} desverificado exitosamente por usuario {}", postId, currentUser.getId());
        
        return ApiResponse.builder()
            .message("Verificación removida exitosamente")
            .build();
    }
}
