package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.ApiResponse;
import com.mypresentpast.backend.dto.MediaDto;
import com.mypresentpast.backend.enums.MediaType;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Media;
import com.mypresentpast.backend.repository.MediaRepository;
import com.mypresentpast.backend.service.CloudinaryService;
import com.mypresentpast.backend.service.MediaService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementación del servicio de Media.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public List<MediaDto> listAvailableMedia() {
        log.info("Listando imágenes disponibles");

        List<Media> availableMedia = mediaRepository.findAvailableMedia();

        return availableMedia.stream()
            .map(this::mapToMediaDto)
            .collect(Collectors.toList());
    }

    @Override
    public MediaDto uploadImage(MultipartFile file) {
        log.info("Subiendo imagen: {}", file.getOriginalFilename());

        // Validar que es una imagen
        if (!isValidImage(file)) {
            throw new IllegalArgumentException("El archivo debe ser una imagen válida (JPG, PNG, GIF)");
        }

        try {
            // Subir a Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.upload(file);

            // Crear registro en BD
            Media media = Media.builder()
                .type(MediaType.IMAGE)
                .url((String) uploadResult.get("url"))
                .cloudinaryId((String) uploadResult.get("public_id"))
                .post(null) // Sin post asignado inicialmente
                .build();

            media = mediaRepository.save(media);
            log.info("Imagen guardada en BD con ID: {}", media.getId());

            return mapToMediaDto(media);

        } catch (Exception e) {
            log.error("Error al subir imagen: {}", e.getMessage(), e);
            throw new RuntimeException("Error al subir imagen: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse deleteImage(Long mediaId) {
        log.info("Eliminando imagen con ID: {}", mediaId);

        Media media = mediaRepository.findById(mediaId)
            .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con id: " + mediaId));

        try {
            // Eliminar de Cloudinary si tiene cloudinaryId
            if (media.getCloudinaryId() != null) {
                cloudinaryService.delete(media.getCloudinaryId());
            }

            // Eliminar de BD
            mediaRepository.delete(media);
            log.info("Imagen eliminada exitosamente");

            return ApiResponse.builder()
                .message("Imagen eliminada con éxito")
                .build();

        } catch (Exception e) {
            log.error("Error al eliminar imagen: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar imagen: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Media getById(Long id) {
        return mediaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return mediaRepository.existsById(id);
    }

    /**
     * Valida si el archivo es una imagen válida.
     */
    private boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && (
            contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif")
        );
    }

    /**
     * Mapea Media a MediaDto.
     */
    private MediaDto mapToMediaDto(Media media) {
        return MediaDto.builder()
            .id(media.getId())
            .type(media.getType())
            .url(media.getUrl())
            .build();
    }
} 