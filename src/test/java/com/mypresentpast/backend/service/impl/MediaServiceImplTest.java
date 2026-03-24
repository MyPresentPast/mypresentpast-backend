package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.MediaDto;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.enums.MediaType;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Media;
import com.mypresentpast.backend.repository.MediaRepository;
import com.mypresentpast.backend.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {

    // ── Mocks ──────────────────────────────────────────────────────────────
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private MediaServiceImpl mediaService;

    // ── Datos de test reutilizables ────────────────────────────────────────
    private Media testMedia;

    // ── Setup ──────────────────────────────────────────────────────────────
    @BeforeEach
    void setUp() {
        testMedia = Media.builder()
                .id(1L)
                .type(MediaType.IMAGE)
                .url("https://cloudinary.com/image.jpg")
                .cloudinaryId("cloudinary_id_123")
                .post(null)
                .build();
    }

    // ── listAvailableMedia ─────────────────────────────────────────────────

    // Verifica que las imágenes disponibles se retornan correctamente mapeadas a DTOs.
    @Test
    void listAvailableMedia_WithMedia_ReturnsMappedDtoList() {
        when(mediaRepository.findAvailableMedia()).thenReturn(List.of(testMedia));

        List<MediaDto> result = mediaService.listAvailableMedia();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(MediaType.IMAGE, result.get(0).getType());
        assertEquals("https://cloudinary.com/image.jpg", result.get(0).getUrl());
        verify(mediaRepository).findAvailableMedia();
    }

    // Verifica que si no hay imágenes disponibles se retorna una lista vacía.
    @Test
    void listAvailableMedia_NoMedia_ReturnsEmptyList() {
        when(mediaRepository.findAvailableMedia()).thenReturn(List.of());

        List<MediaDto> result = mediaService.listAvailableMedia();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── uploadImage ────────────────────────────────────────────────────────

    // Verifica que una imagen JPEG válida se sube a Cloudinary, se persiste en la BD y se retorna el DTO.
    @Test
    void uploadImage_ValidJpegFile_UploadsToCloudinaryAndReturnsDto() {
        MultipartFile file = validImageMock("image/jpeg");
        when(cloudinaryService.upload(file))
                .thenReturn(Map.of("url", "https://cloudinary.com/new.jpg", "public_id", "new_public_id"));

        Media expectedMedia = Media.builder()
                .type(MediaType.IMAGE)
                .url("https://cloudinary.com/new.jpg")
                .cloudinaryId("new_public_id")
                .post(null)
                .build();
        when(mediaRepository.save(refEq(expectedMedia, "id"))).thenReturn(testMedia);

        MediaDto result = mediaService.uploadImage(file);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(MediaType.IMAGE, result.getType());
        assertEquals("https://cloudinary.com/image.jpg", result.getUrl());
        verify(cloudinaryService).upload(file);
        verify(mediaRepository).save(refEq(expectedMedia, "id"));
    }

    // Verifica que pasar un archivo nulo lanza NullPointerException: el logger llama
    @Test
    void uploadImage_NullFile_ThrowsNullPointerException() {
        // file.getOriginalFilename() antes de llegar a la validación de isValidImage.
        assertThrows(NullPointerException.class, () -> mediaService.uploadImage(null));

        verify(cloudinaryService, never()).upload(any());
        verify(mediaRepository, never()).save(any());
    }

    // Verifica que pasar un archivo vacío lanza IllegalArgumentException.
    @Test
    void uploadImage_EmptyFile_ThrowsIllegalArgumentException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> mediaService.uploadImage(file));

        verify(cloudinaryService, never()).upload(any());
    }

    // Verifica que un archivo con tipo de contenido no permitido lanza IllegalArgumentException.
    @Test
    void uploadImage_InvalidContentType_ThrowsIllegalArgumentException() {
        MultipartFile file = validImageMock("application/pdf");

        assertThrows(IllegalArgumentException.class, () -> mediaService.uploadImage(file));

        verify(cloudinaryService, never()).upload(any());
    }

    // ── deleteImage ────────────────────────────────────────────────────────

    // Verifica que eliminar una imagen con cloudinaryId la borra de Cloudinary y de la BD.
    @Test
    void deleteImage_WithCloudinaryId_DeletesFromCloudinaryAndDb() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(testMedia));

        ApiResponse response = mediaService.deleteImage(1L);

        assertNotNull(response);
        assertEquals("Imagen eliminada con éxito", response.getMessage());
        verify(cloudinaryService).delete("cloudinary_id_123");
        verify(mediaRepository).delete(testMedia);
    }

    // Verifica que eliminar una imagen sin cloudinaryId omite Cloudinary y solo borra de la BD.
    @Test
    void deleteImage_WithoutCloudinaryId_DeletesOnlyFromDb() {
        testMedia.setCloudinaryId(null);
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(testMedia));

        ApiResponse response = mediaService.deleteImage(1L);

        assertNotNull(response);
        assertEquals("Imagen eliminada con éxito", response.getMessage());
        verify(cloudinaryService, never()).delete(any());
        verify(mediaRepository).delete(testMedia);
    }

    // Verifica que eliminar una imagen inexistente lanza ResourceNotFoundException.
    @Test
    void deleteImage_NotFound_ThrowsResourceNotFoundException() {
        when(mediaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mediaService.deleteImage(99L));

        verify(cloudinaryService, never()).delete(any());
        verify(mediaRepository, never()).delete(any());
    }

    // ── getById ────────────────────────────────────────────────────────────

    // Verifica que buscar una imagen por ID existente retorna la entidad correcta.
    @Test
    void getById_ExistingId_ReturnsMedia() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(testMedia));

        Media result = mediaService.getById(1L);

        assertNotNull(result);
        assertEquals(testMedia, result);
        verify(mediaRepository).findById(1L);
    }

    // Verifica que buscar una imagen con ID inexistente lanza ResourceNotFoundException.
    @Test
    void getById_NotFound_ThrowsResourceNotFoundException() {
        when(mediaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mediaService.getById(99L));
    }

    // ── existsById ─────────────────────────────────────────────────────────

    // Verifica que existsById retorna true cuando la imagen existe.
    @Test
    void existsById_ExistingId_ReturnsTrue() {
        when(mediaRepository.existsById(1L)).thenReturn(true);

        assertTrue(mediaService.existsById(1L));
        verify(mediaRepository).existsById(1L);
    }

    // Verifica que existsById retorna false cuando la imagen no existe.
    @Test
    void existsById_NonExistingId_ReturnsFalse() {
        when(mediaRepository.existsById(99L)).thenReturn(false);

        assertFalse(mediaService.existsById(99L));
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * Crea un mock de MultipartFile con el contentType dado y isEmpty=false.
     */
    private MultipartFile validImageMock(String contentType) {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(contentType);
        return file;
    }
}
