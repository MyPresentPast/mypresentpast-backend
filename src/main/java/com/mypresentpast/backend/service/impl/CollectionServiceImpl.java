package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.LocationDto;
import com.mypresentpast.backend.dto.MediaDto;
import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.dto.request.CreateCollectionAndSavePostRequest;
import com.mypresentpast.backend.dto.request.CreateCollectionRequest;
import com.mypresentpast.backend.dto.request.UpdateCollectionRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.CollectionSummaryResponse;
import com.mypresentpast.backend.dto.response.PostCollectionStatusResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Collection;
import com.mypresentpast.backend.model.CollectionPost;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.CollectionPostRepository;
import com.mypresentpast.backend.repository.CollectionRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.CollectionService;
import com.mypresentpast.backend.utils.SecurityUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio para operaciones relacionadas con Collection.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CollectionServiceImpl implements CollectionService {

    private static final int MAX_COLLECTIONS_PER_USER = 20;
    private static final int MAX_POSTS_PER_COLLECTION = 20;

    private final CollectionRepository collectionRepository;
    private final CollectionPostRepository collectionPostRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CollectionSummaryResponse> getMyCollections() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        List<Object[]> collectionsWithCount = collectionRepository
                .findCollectionsWithPostCountByAuthorId(currentUserId);
        
        return collectionsWithCount.stream()
                .map(result -> {
                    Collection collection = (Collection) result[0];
                    Long postCount = (Long) result[1];
                    
                    return CollectionSummaryResponse.builder()
                            .id(collection.getId())
                            .name(collection.getName())
                            .description(collection.getDescription())
                            .postCount(postCount.intValue())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public ApiResponse createCollection(CreateCollectionRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // Validar límite de colecciones por usuario
        long userCollectionCount = collectionRepository.countByAuthorId(currentUserId);
        if (userCollectionCount >= MAX_COLLECTIONS_PER_USER) {
            throw new BadRequestException("Has alcanzado el límite máximo de " + MAX_COLLECTIONS_PER_USER + " colecciones");
        }
        
        // Validar nombre único por usuario
        if (collectionRepository.existsByNameAndAuthorId(request.getName(), currentUserId)) {
            throw new BadRequestException("Ya tienes una colección con ese nombre");
        }
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        Collection collection = Collection.builder()
                .name(request.getName())
                .description(request.getDescription())
                .author(currentUser)
                .build();
        
        collectionRepository.save(collection);
        
        return ApiResponse.builder()
                .message("Colección creada con éxito")
                .build();
    }

    @Override
    public ApiResponse updateCollection(Long id, UpdateCollectionRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        Collection collection = collectionRepository.findByIdAndAuthorId(id, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Colección no encontrada"));
        
        // Validar nombre único si se está cambiando
        if (request.getName() != null && !request.getName().equals(collection.getName())) {
            if (collectionRepository.existsByNameAndAuthorIdAndIdNot(request.getName(), currentUserId, id)) {
                throw new BadRequestException("Ya tienes una colección con ese nombre");
            }
            collection.setName(request.getName());
        }
        
        // Actualizar descripción si se proporciona
        if (request.getDescription() != null) {
            collection.setDescription(request.getDescription());
        }
        
        collection.setUpdatedAt(LocalDateTime.now());
        collectionRepository.save(collection);
        
        return ApiResponse.builder()
                .message("Colección actualizada con éxito")
                .build();
    }

    @Override
    public ApiResponse deleteCollection(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        Collection collection = collectionRepository.findByIdAndAuthorId(id, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Colección no encontrada"));
        
        collectionRepository.delete(collection);
        
        return ApiResponse.builder()
                .message("Colección eliminada con éxito")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getCollectionPosts(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // Verificar que la colección pertenece al usuario
        Collection collection = collectionRepository.findByIdAndAuthorId(id, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Colección no encontrada"));
        
        List<CollectionPost> collectionPosts = collectionPostRepository
                .findByCollectionIdOrderByAddedAtDesc(id);
        
        return collectionPosts.stream()
                .map(cp -> mapToPostResponse(cp.getPost()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PostCollectionStatusResponse getPostCollectionStatus(Long postId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // Verificar que el post existe y está activo
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado"));
        
        if (post.getStatus() != PostStatus.ACTIVE) {
            throw new BadRequestException("El post no está disponible");
        }
        
        List<CollectionPost> collectionPosts = collectionPostRepository
                .findByPostIdAndCollectionAuthorId(postId, currentUserId);
        
        List<PostCollectionStatusResponse.CollectionInfo> collectionsContaining = 
                collectionPosts.stream()
                        .map(cp -> PostCollectionStatusResponse.CollectionInfo.builder()
                                .collectionId(cp.getCollection().getId())
                                .collectionName(cp.getCollection().getName())
                                .build())
                        .collect(Collectors.toList());
        
        return PostCollectionStatusResponse.builder()
                .postId(postId)
                .collectionsContaining(collectionsContaining)
                .build();
    }

    @Override
    public ApiResponse addPostToCollection(Long collectionId, Long postId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // Verificar que la colección pertenece al usuario
        Collection collection = collectionRepository.findByIdAndAuthorId(collectionId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Colección no encontrada"));
        
        // Verificar que el post existe y está activo
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado"));
        
        if (post.getStatus() != PostStatus.ACTIVE) {
            throw new BadRequestException("El post no está disponible");
        }
        
        // Verificar que no esté ya en la colección
        if (collectionPostRepository.existsByCollectionIdAndPostId(collectionId, postId)) {
            throw new BadRequestException("El post ya está en esta colección");
        }
        
        // Verificar límite de posts por colección
        long collectionPostCount = collectionPostRepository.countByCollectionId(collectionId);
        if (collectionPostCount >= MAX_POSTS_PER_COLLECTION) {
            throw new BadRequestException("La colección ha alcanzado el límite máximo de " + MAX_POSTS_PER_COLLECTION + " posts");
        }
        
        CollectionPost collectionPost = CollectionPost.builder()
                .collection(collection)
                .post(post)
                .build();
        
        collectionPostRepository.save(collectionPost);
        
        return ApiResponse.builder()
                .message("Post agregado a la colección")
                .build();
    }

    @Override
    public ApiResponse removePostFromCollection(Long collectionId, Long postId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // Verificar que la colección pertenece al usuario
        Collection collection = collectionRepository.findByIdAndAuthorId(collectionId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Colección no encontrada"));
        
        // Verificar que el post está en la colección
        CollectionPost collectionPost = collectionPostRepository
                .findByCollectionIdAndPostId(collectionId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("El post no está en esta colección"));
        
        collectionPostRepository.delete(collectionPost);
        
        return ApiResponse.builder()
                .message("Post eliminado de la colección")
                .build();
    }

    @Override
    public ApiResponse createCollectionAndSavePost(CreateCollectionAndSavePostRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // Validar límite de colecciones por usuario
        long userCollectionCount = collectionRepository.countByAuthorId(currentUserId);
        if (userCollectionCount >= MAX_COLLECTIONS_PER_USER) {
            throw new BadRequestException("Has alcanzado el límite máximo de " + MAX_COLLECTIONS_PER_USER + " colecciones");
        }
        
        // Validar nombre único por usuario
        if (collectionRepository.existsByNameAndAuthorId(request.getName(), currentUserId)) {
            throw new BadRequestException("Ya tienes una colección con ese nombre");
        }
        
        // Verificar que el post existe y está activo
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado"));
        
        if (post.getStatus() != PostStatus.ACTIVE) {
            throw new BadRequestException("El post no está disponible");
        }
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Crear la colección
        Collection collection = Collection.builder()
                .name(request.getName())
                .description(request.getDescription())
                .author(currentUser)
                .build();
        
        collection = collectionRepository.save(collection);
        
        // Agregar el post a la colección
        CollectionPost collectionPost = CollectionPost.builder()
                .collection(collection)
                .post(post)
                .build();
        
        collectionPostRepository.save(collectionPost);
        
        return ApiResponse.builder()
                .message("Colección creada y post guardado con éxito")
                .build();
    }

    /**
     * Mapea un Post a PostResponse.
     */
    private PostResponse mapToPostResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setPostedAt(post.getPostedAt());
        response.setDate(post.getDate());
        response.setIsByIA(post.getIsByIA());
        response.setIsVerified(post.getIsVerified());
        response.setCategory(post.getCategory());
        response.setStatus(post.getStatus());

        // Mapear autor manualmente
        if (post.getAuthor() != null) {
            UserDto authorDto = new UserDto();
            authorDto.setId(post.getAuthor().getId());
            authorDto.setName(post.getAuthor().getProfileUsername());
            authorDto.setType(post.getAuthor().getRole());
            authorDto.setAvatar(post.getAuthor().getAvatar());
            response.setAuthor(authorDto);
        }

        // Mapear ubicación manualmente
        if (post.getLocation() != null) {
            LocationDto locationDto = new LocationDto();
            locationDto.setId(post.getLocation().getId());
            locationDto.setAddress(post.getLocation().getAddress());
            locationDto.setLatitude(post.getLocation().getLatitude());
            locationDto.setLongitude(post.getLocation().getLongitude());
            response.setLocation(locationDto);
        }

        // Mapear media manualmente
        if (post.getMedia() != null && !post.getMedia().isEmpty()) {
            List<MediaDto> mediaDtos = post.getMedia().stream()
                    .map(media -> {
                        MediaDto mediaDto = new MediaDto();
                        mediaDto.setId(media.getId());
                        mediaDto.setType(media.getType());
                        mediaDto.setUrl(media.getUrl());
                        return mediaDto;
                    })
                    .collect(Collectors.toList());
            response.setMedia(mediaDtos);
        }

        return response;
    }
}
