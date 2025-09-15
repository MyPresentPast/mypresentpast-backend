package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.LocationDto;
import com.mypresentpast.backend.dto.MediaDto;
import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.dto.request.CreatePostRequest;
import com.mypresentpast.backend.dto.request.UpdatePostRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.MapResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import com.mypresentpast.backend.enums.Category;
import com.mypresentpast.backend.enums.MediaType;
import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Location;
import com.mypresentpast.backend.model.Media;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.LocationRepository;
import com.mypresentpast.backend.repository.MediaRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.CloudinaryService;
import com.mypresentpast.backend.service.LikeService;
import com.mypresentpast.backend.service.PostService;
import com.mypresentpast.backend.service.PostVerificationQueryService;
import com.mypresentpast.backend.utils.SecurityUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementación del servicio de Post.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final MediaRepository mediaRepository;
    private final CloudinaryService cloudinaryService;
    private final LikeService likeService;
    private final PostVerificationQueryService verificationQueryService;

    @Override
    public ApiResponse createPost(CreatePostRequest request, List<MultipartFile> images) {
        log.info("Creando nuevo post: {}", request.getTitle());

        // 1. Validar que el autor existe
        User author = userRepository.findById(request.getAuthorId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getAuthorId()));

        // 2. Buscar si ya existe una ubicación con coordenadas similares
        Location location = findOrCreateLocation(request.getLatitude(), request.getLongitude(), request.getAddress());

        // 3. Crear el post
        Post post = Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .date(request.getDate())
            .postedAt(LocalDate.now())
            .category(request.getCategory())
            .isByIA(request.getIsByIA())
            .status(PostStatus.ACTIVE)
            .author(author)
            .location(location)
            .build();

        // 4. Guardar el post primero para obtener el ID
        post = postRepository.save(post);
        log.info("Post guardado con ID: {}", post.getId());

        // 5. Subir imágenes a Cloudinary y crear objetos Media
        if (images != null && !images.isEmpty()) {
            // Validar límite de imágenes
            if (images.size() > 5) {
                throw new IllegalArgumentException("Máximo 5 imágenes permitidas por publicación");
            }

            List<Media> mediaList = new ArrayList<>();
            for (MultipartFile image : images) {
                try {
                    // Subir a Cloudinary
                    Map<String, Object> uploadResult = cloudinaryService.upload(image);

                    // Crear registro Media
                    Media media = Media.builder()
                        .type(MediaType.IMAGE)
                        .url((String) uploadResult.get("url"))
                        .cloudinaryId((String) uploadResult.get("public_id"))
                        .post(post)
                        .build();

                    mediaList.add(media);

                } catch (Exception e) {
                    log.error("Error al subir imagen {}: {}", image.getOriginalFilename(), e.getMessage());
                    throw new RuntimeException("Error al subir imagen: " + e.getMessage());
                }
            }

            // Guardar todos los Media
            mediaRepository.saveAll(mediaList);
            post.setMedia(mediaList);
            log.info("Se agregaron {} imágenes al post", mediaList.size());
        }

        return ApiResponse.builder()
            .message("Publicación creada con éxito")
            .build();
    }

    /**
     * Busca una ubicación existente por proximidad o crea una nueva.
     * Ahora recibe coordenadas directas del frontend.
     */
    private Location findOrCreateLocation(Double latitude, Double longitude, String address) {
        // Buscar ubicaciones existentes dentro de un rango de proximidad
        List<Location> existingLocations = locationRepository.findLocationsByProximity(latitude, longitude);

        if (!existingLocations.isEmpty()) {
            Location existingLocation = existingLocations.get(0);
            log.info("Reutilizando ubicación existente: {} (ID: {})",
                existingLocation.getAddress(), existingLocation.getId());
            return existingLocation;
        }

        // Crear nueva ubicación con los datos del frontend
        Location newLocation = Location.builder()
            .address(address)
            .latitude(latitude)
            .longitude(longitude)
            .build();

        newLocation = locationRepository.save(newLocation);
        log.info("Nueva ubicación creada: {} (ID: {}) en coordenadas ({}, {})",
            newLocation.getAddress(), newLocation.getId(), latitude, longitude);

        return newLocation;
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con id: " + id));
        
        return mapToPostResponse(post);
    }

    @Override
    public List<PostResponse> getPostsByUser(final Long id) {
        List<Post> posts = postRepository.findByAuthorId(id);

        if (posts.isEmpty()) {
            throw new ResourceNotFoundException("No hay publicaciones disponibles para mostrar");
        }

        List<PostResponse> postResponses = new ArrayList<>();

        for (Post post : posts) {
            postResponses.add(mapToPostResponse(post));
        }

        return postResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getLikedPostsByCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Post> likedPosts = postRepository.findLikedPostsByUserId(currentUserId);

        if (likedPosts.isEmpty()) {
            log.info("Usuario {} no tiene posts likeados", currentUserId);
            return new ArrayList<>();
        }

        List<PostResponse> postResponses = new ArrayList<>();
        for (Post post : likedPosts) {
            postResponses.add(mapToPostResponse(post));
        }

        log.info("Usuario {} tiene {} posts likeados", currentUserId, postResponses.size());
        return postResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public MapResponse getMapData(double latMin, double latMax, double lonMin, double lonMax, String category, LocalDate date, Boolean isVerified, Boolean isByIA, Long userId) {

        // 1. Convertir category string a Category enum
        Category categoryEnum = null;
        if (category != null && !category.isEmpty()) {
            try {
                categoryEnum = Category.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Categoría inválida: {}", category);
                // Si la categoría es inválida, la dejamos como null
            }
        }

        // 2. Usar query con filtros básicos (sin isVerified)
        String categoryString = (categoryEnum != null) ? categoryEnum.name() : "";
        List<Post> posts = postRepository.findPostsInAreaWithFilters(
            latMin, latMax, lonMin, lonMax, categoryString, date, isByIA, userId
        );

        log.info("Encontrados {} posts en área ({},{}) a ({},{}) con filtros: category={}, date={}, isVerified={}, isByIA={}, userId={}",
            posts.size(), latMin, lonMin, latMax, lonMax, category, date, isVerified, isByIA, userId);

        // 3. Filtrar por verificación si es necesario y convertir a DTOs
        List<PostResponse> postResponses = posts.stream()
            .filter(post -> isVerified == null || verificationQueryService.isPostVerified(post) == isVerified)
            .map(this::mapToPostResponse)
            .collect(Collectors.toList());

        return MapResponse.builder()
            .posts(postResponses)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getRandomPost() {
        // Obtener todos los posts activos con ubicación
        List<Post> activePosts = postRepository.findByStatusAndLocationIsNotNull(PostStatus.ACTIVE);

        if (activePosts.isEmpty()) {
            throw new ResourceNotFoundException("No hay publicaciones disponibles para mostrar");
        }

        // Seleccionar post aleatorio
        Post randomPost = activePosts.get(new Random().nextInt(activePosts.size()));

        log.info("Post aleatorio seleccionado: '{}' en ubicación ({}, {})",
            randomPost.getTitle(),
            randomPost.getLocation().getLatitude(),
            randomPost.getLocation().getLongitude());

        return mapToPostResponse(randomPost);
    }

    @Override
    public ApiResponse updatePost(Long id, UpdatePostRequest request, List<MultipartFile> newImages) {
        log.info("Actualizando post con ID: {}", id);

        // 1. Buscar el post existente
        Post existingPost = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con id: " + id));

        // 2. Validar que el autor existe
        User author = userRepository.findById(request.getAuthorId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getAuthorId()));

        // 3. Buscar o crear ubicación si cambió
        Location location = findOrCreateLocation(request.getLatitude(), request.getLongitude(), request.getAddress());

        // 4. Actualizar los campos del post
        existingPost.setTitle(request.getTitle());
        existingPost.setContent(request.getContent());
        existingPost.setDate(request.getDate());
        existingPost.setCategory(request.getCategory());
        existingPost.setAuthor(author);
        existingPost.setLocation(location);
        existingPost.setIsByIA(request.getIsByIA());

        // 5. TRABAJAR CON LA COLECCIÓN ORIGINAL (CLAVE DEL FIX)
        List<Media> currentMedia = existingPost.getMedia();
        if (currentMedia == null) {
            currentMedia = new ArrayList<>();
            existingPost.setMedia(currentMedia);
        }

        List<Long> keepImageIds = request.getKeepImageIds() != null ? request.getKeepImageIds() : new ArrayList<>();


        // 6. Identificar imágenes a eliminar de Cloudinary ANTES de eliminar de la colección
        List<Media> imagesToDeleteFromCloudinary = new ArrayList<>();
        List<Media> imagesToKeep = new ArrayList<>();

        for (Media media : currentMedia) {
            if (keepImageIds.contains(media.getId())) {
                imagesToKeep.add(media);
            } else {
                imagesToDeleteFromCloudinary.add(media);
            }
        }

        // 7. Eliminar de Cloudinary PRIMERO (antes de eliminar de BD)
        for (Media mediaToDelete : imagesToDeleteFromCloudinary) {
            if (mediaToDelete.getCloudinaryId() != null && !mediaToDelete.getCloudinaryId().trim().isEmpty()) {
                try {
                    cloudinaryService.delete(mediaToDelete.getCloudinaryId());
                    log.info("Imagen eliminada de Cloudinary: {}", mediaToDelete.getCloudinaryId());
                } catch (Exception e) {
                    log.error("Error al eliminar imagen de Cloudinary {}: {}", mediaToDelete.getId(), e.getMessage());
                }
            }
        }

        // 8. ✅ MODIFICAR LA COLECCIÓN EXISTENTE (no reemplazar)
        // Hibernate eliminará automáticamente de la BD debido a orphanRemoval = true
        currentMedia.removeIf(media -> !keepImageIds.contains(media.getId()));

        // 9. Agregar nuevas imágenes
        if (newImages != null && !newImages.isEmpty()) {
            // Validar límite total de imágenes
            if (keepImageIds.size() + newImages.size() > 5) {
                throw new IllegalArgumentException("Máximo 5 imágenes permitidas por publicación");
            }

            for (MultipartFile newImage : newImages) {
                try {
                    // Subir a Cloudinary
                    Map<String, Object> uploadResult = cloudinaryService.upload(newImage);

                    // Crear registro Media
                    Media media = Media.builder()
                        .type(MediaType.IMAGE)
                        .url((String) uploadResult.get("url"))
                        .cloudinaryId((String) uploadResult.get("public_id"))
                        .post(existingPost)
                        .build();

                    // Agregar a la colección existente
                    currentMedia.add(media);

                } catch (Exception e) {
                    log.error("Error al procesar imagen {}: {}", newImage.getOriginalFilename(), e.getMessage());
                    throw new RuntimeException("Error al subir imagen: " + e.getMessage());
                }
            }
        }

        // 10. Guardar cambios
        postRepository.save(existingPost);
        log.info("Post actualizado exitosamente");

        return ApiResponse.builder()
            .message("Publicación actualizada con éxito")
            .build();
    }

    @Override
    public ApiResponse deletePost(Long id) {
        log.info("Eliminando post con ID: {}", id);

        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con id: " + id));

        // Eliminación lógica
        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);

        log.info("Post eliminado lógicamente: {}", post.getTitle());

        return ApiResponse.builder()
            .message("Publicación eliminada con éxito")
            .build();
    }

    @Override
    public ApiResponse deletePostMedia(Long postId, Long mediaId) {
        log.info("Eliminando imagen {} del post {}", mediaId, postId);

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con id: " + postId));

        // Buscar la imagen a eliminar ANTES de eliminarla para obtener cloudinaryId
        Media mediaToDelete = post.getMedia().stream()
            .filter(media -> media.getId().equals(mediaId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con id: " + mediaId));

        // Eliminar de Cloudinary PRIMERO
        if (mediaToDelete.getCloudinaryId() != null && !mediaToDelete.getCloudinaryId().trim().isEmpty()) {
            try {
                log.info("Eliminando imagen de Cloudinary: {} (ID: {})", mediaToDelete.getCloudinaryId(), mediaId);
                Map<String, Object> deleteResult = cloudinaryService.delete(mediaToDelete.getCloudinaryId());
                log.info("Resultado eliminación Cloudinary: {}", deleteResult);
            } catch (Exception e) {
                log.error("ERROR al eliminar imagen de Cloudinary {}: {}", mediaToDelete.getCloudinaryId(), e.getMessage(), e);
                // Continuamos para eliminar de BD aunque falle Cloudinary
            }
        }

        // Ahora eliminar de la BD
        boolean removed = post.getMedia().removeIf(media -> media.getId().equals(mediaId));

        if (!removed) {
            throw new ResourceNotFoundException("Error inesperado: no se pudo eliminar la imagen de la BD");
        }

        postRepository.save(post);
        log.info("Imagen eliminada exitosamente del post y Cloudinary");

        return ApiResponse.builder()
            .message("Imagen eliminada con éxito")
            .build();
    }

    @Override
    public ApiResponse addPostMedia(Long postId, List<String> imageUrls) {
        log.info("Agregando {} imágenes al post {}", imageUrls.size(), postId);

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con id: " + postId));

        // Validar que no exceda el límite de 5 imágenes
        int currentImageCount = post.getMedia() != null ? post.getMedia().size() : 0;
        if (currentImageCount + imageUrls.size() > 5) {
            throw new IllegalArgumentException("No se pueden agregar más imágenes. Límite máximo: 5 imágenes por publicación");
        }

        // Crear nuevas imágenes
        List<Media> newMedia = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Media media = Media.builder()
                .type(MediaType.IMAGE)
                .url(imageUrl)
                .post(post)
                .build();
            newMedia.add(media);
        }

        // Agregar a la lista existente
        if (post.getMedia() == null) {
            post.setMedia(new ArrayList<>());
        }
        post.getMedia().addAll(newMedia);

        postRepository.save(post);
        log.info("Se agregaron {} imágenes al post exitosamente", newMedia.size());

        return ApiResponse.builder()
            .message("Imágenes agregadas con éxito")
            .build();
    }

    // Mapeo manual simplificado
    private PostResponse mapToPostResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setPostedAt(post.getPostedAt());
        response.setDate(post.getDate());
        response.setIsByIA(post.getIsByIA());
        response.setIsVerified(verificationQueryService.isPostVerified(post));
        response.setCategory(post.getCategory());

        // Mapear quién verificó el post externamente (si existe)
        User externalVerifier = verificationQueryService.getExternalVerifier(post.getId());
        if (externalVerifier != null) {
            UserDto verifiedByDto = new UserDto();
            verifiedByDto.setId(externalVerifier.getId());
            verifiedByDto.setName(externalVerifier.getProfileUsername());
            verifiedByDto.setType(externalVerifier.getRole());
            verifiedByDto.setAvatar(externalVerifier.getAvatar());
            response.setVerifiedBy(verifiedByDto);
        }
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
            List<MediaDto> mediaList = post.getMedia().stream()
                .map(media -> {
                    MediaDto mediaDto = new MediaDto();
                    mediaDto.setId(media.getId());
                    mediaDto.setType(media.getType());
                    mediaDto.setUrl(media.getUrl());
                    return mediaDto;
                })
                .collect(Collectors.toList());
            response.setMedia(mediaList);
        } else {
            response.setMedia(new ArrayList<>());
        }

        Long totalLikes = likeService.getTotalLikes(post.getId());
        response.setTotalLikes(totalLikes);
        
        Boolean isLiked = likeService.isLikedByCurrentUser(post.getId());
        response.setIsLiked(isLiked);

        return response;
    }
} 