package com.mypresentpast.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.mypresentpast.backend.exception.CloudinaryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Cloudinary se construye internamente en el constructor con @Value, por lo que se inyecta
// el mock via reflexión. El stub de uploader() se configura en cada test que lo necesita
// para respetar strict stubbing sin UnnecessaryStubbingException.
@ExtendWith(MockitoExtension.class)
class CloudinaryServiceImplTest {

    @Mock
    private Cloudinary mockCloudinary;
    @Mock
    private Uploader mockUploader;

    private CloudinaryServiceImpl cloudinaryService;

    @BeforeEach
    void setUp() throws Exception {
        // Crear instancia con valores dummy (los @Value no se usan en tests)
        cloudinaryService = new CloudinaryServiceImpl("test-cloud", "test-key", "test-secret");

        // Inyectar el mock de Cloudinary en el campo private final via reflexión
        Field cloudinaryField = CloudinaryServiceImpl.class.getDeclaredField("cloudinary");
        cloudinaryField.setAccessible(true);
        cloudinaryField.set(cloudinaryService, mockCloudinary);
    }

    // ── upload(MultipartFile) ──────────────────────────────────────────────

    // Verifica que una imagen JPEG válida se convierte, sube a Cloudinary y retorna el resultado.
    @Test
    void upload_ValidJpegFile_UploadsToCloudinaryAndReturnsResult() throws Exception {
        MultipartFile file = validImageMock("image/jpeg");
        when(mockCloudinary.uploader()).thenReturn(mockUploader);
        when(mockUploader.upload(any(Object.class), any(Map.class))).thenReturn(
                Map.of("url", "https://cloudinary.com/result.jpg", "public_id", "test_id"));

        Map<String, Object> result = cloudinaryService.upload(file);

        assertNotNull(result);
        assertEquals("https://cloudinary.com/result.jpg", result.get("url"));
        verify(mockUploader).upload(any(Object.class), any(Map.class));
    }

    // Verifica que un archivo mayor a 10MB lanza RuntimeException antes de intentar la subida.
    @Test
    void upload_FileTooLarge_ThrowsRuntimeException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(11L * 1024 * 1024);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cloudinaryService.upload(file));

        assertTrue(ex.getMessage().contains("Error al subir imagen"));
        verify(mockUploader, never()).upload(any(), any());
    }

    // Verifica que un tipo de contenido no permitido lanza RuntimeException antes de intentar la subida.
    @Test
    void upload_InvalidContentType_ThrowsRuntimeException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1024L);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cloudinaryService.upload(file));

        assertTrue(ex.getMessage().contains("Error al subir imagen"));
        verify(mockUploader, never()).upload(any(), any());
    }

    // ── uploadAvatar ───────────────────────────────────────────────────────

    // Verifica que subir un avatar válido llama a Cloudinary con el publicId correcto y retorna el resultado.
    @Test
    void uploadAvatar_ValidFile_ReturnsUploadResult() throws Exception {
        MultipartFile file = validAvatarFileMock("image/jpeg");
        when(mockCloudinary.uploader()).thenReturn(mockUploader);
        when(mockUploader.upload(any(byte[].class), any(Map.class))).thenReturn(
                Map.of("secure_url", "https://cloudinary.com/avatar.jpg"));

        Map<String, Object> result = cloudinaryService.uploadAvatar(file, 1L);

        assertNotNull(result);
        assertEquals("https://cloudinary.com/avatar.jpg", result.get("secure_url"));
        verify(mockUploader).upload(any(byte[].class), any(Map.class));
    }

    // Verifica que pasar un archivo nulo lanza CloudinaryException (validateImage detecta null).
    @Test
    void uploadAvatar_NullFile_ThrowsCloudinaryException() throws Exception {
        assertThrows(CloudinaryException.class, () -> cloudinaryService.uploadAvatar(null, 1L));

        verify(mockUploader, never()).upload(any(), any());
    }

    // Verifica que pasar un archivo vacío lanza CloudinaryException.
    @Test
    void uploadAvatar_EmptyFile_ThrowsCloudinaryException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(CloudinaryException.class, () -> cloudinaryService.uploadAvatar(file, 1L));

        verify(mockUploader, never()).upload(any(), any());
    }

    // Verifica que un avatar mayor a 10MB lanza CloudinaryException.
    @Test
    void uploadAvatar_FileTooLarge_ThrowsCloudinaryException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(11L * 1024 * 1024);

        assertThrows(CloudinaryException.class, () -> cloudinaryService.uploadAvatar(file, 1L));

        verify(mockUploader, never()).upload(any(), any());
    }

    // Verifica que un avatar con tipo de contenido no permitido lanza CloudinaryException.
    @Test
    void uploadAvatar_InvalidContentType_ThrowsCloudinaryException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/gif"); // gif no está permitido en avatars

        assertThrows(CloudinaryException.class, () -> cloudinaryService.uploadAvatar(file, 1L));

        verify(mockUploader, never()).upload(any(), any());
    }

    // ── delete ─────────────────────────────────────────────────────────────

    // Verifica que eliminar por publicId llama a Cloudinary destroy y retorna el resultado.
    @Test
    void delete_ValidPublicId_CallsDestroyAndReturnsResult() throws Exception {
        when(mockCloudinary.uploader()).thenReturn(mockUploader);
        when(mockUploader.destroy(eq("cloudinary_id_123"), any(Map.class))).thenReturn(
                Map.of("result", "ok"));

        Map<String, Object> result = cloudinaryService.delete("cloudinary_id_123");

        assertNotNull(result);
        assertEquals("ok", result.get("result"));
        verify(mockUploader).destroy(eq("cloudinary_id_123"), any(Map.class));
    }

    // ── upload(MultipartFile, Map) ─────────────────────────────────────────

    // Verifica que subir con opciones personalizadas las pasa directamente a Cloudinary.
    @Test
    void uploadWithOptions_ValidFile_UploadsWithCustomOptionsAndReturnsResult() throws Exception {
        MultipartFile file = validOptionsFileMock();
        Map<String, Object> customOptions = Map.of("folder", "posts", "resource_type", "image");
        when(mockCloudinary.uploader()).thenReturn(mockUploader);
        when(mockUploader.upload(any(Object.class), eq(customOptions))).thenReturn(
                Map.of("public_id", "posts/test_id", "url", "https://cloudinary.com/post.jpg"));

        Map<String, Object> result = cloudinaryService.upload(file, customOptions);

        assertNotNull(result);
        assertEquals("posts/test_id", result.get("public_id"));
        verify(mockUploader).upload(any(Object.class), eq(customOptions));
    }

    // Verifica que si Cloudinary falla al subir con opciones se lanza CloudinaryException.
    @Test
    void uploadWithOptions_UploaderThrowsException_ThrowsCloudinaryException() throws Exception {
        MultipartFile file = validOptionsFileMock();
        Map<String, Object> options = Map.of("folder", "test");
        when(mockCloudinary.uploader()).thenReturn(mockUploader);
        when(mockUploader.upload(any(Object.class), any(Map.class))).thenThrow(new IOException("connection error"));

        assertThrows(CloudinaryException.class, () -> cloudinaryService.upload(file, options));
    }

    // Verifica que un archivo marcado como vacío falla la validación isValidImageType y lanza RuntimeException.
    @Test
    void upload_EmptyFile_ThrowsRuntimeException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1024L);
        when(file.isEmpty()).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cloudinaryService.upload(file));

        assertTrue(ex.getMessage().contains("Error al subir imagen"));
        verify(mockUploader, never()).upload(any(), any());
    }

    // Verifica que un archivo con contentType null falla la validación isValidImageType y lanza RuntimeException.
    @Test
    void upload_NullContentType_ThrowsRuntimeException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1024L);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cloudinaryService.upload(file));

        assertTrue(ex.getMessage().contains("Error al subir imagen"));
        verify(mockUploader, never()).upload(any(), any());
    }

    // Verifica que si Cloudinary lanza excepción al eliminar, se relanza como RuntimeException.
    @Test
    void delete_UploaderThrowsException_ThrowsRuntimeException() throws Exception {
        when(mockCloudinary.uploader()).thenReturn(mockUploader);
        when(mockUploader.destroy(eq("bad_id"), any(Map.class))).thenThrow(new IOException("network error"));

        assertThrows(RuntimeException.class, () -> cloudinaryService.delete("bad_id"));
    }

    // Verifica que un avatar con contentType null lanza CloudinaryException en validateImage.
    @Test
    void uploadAvatar_NullContentType_ThrowsCloudinaryException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn(null);

        assertThrows(CloudinaryException.class, () -> cloudinaryService.uploadAvatar(file, 1L));

        verify(mockUploader, never()).upload(any(), any());
    }

    // Verifica que si Cloudinary lanza excepción al subir el avatar, se relanza como CloudinaryException.
    @Test
    void uploadAvatar_UploaderThrowsIOException_ThrowsCloudinaryException() throws Exception {
        MultipartFile file = validAvatarFileMock("image/jpeg");
        when(mockCloudinary.uploader()).thenReturn(mockUploader);
        when(mockUploader.upload(any(byte[].class), any(Map.class))).thenThrow(new IOException("connection error"));

        assertThrows(CloudinaryException.class, () -> cloudinaryService.uploadAvatar(file, 1L));
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * Mock para upload(MultipartFile): valida tamaño, tipo y llama a convert() internamente.
     * Necesita: getSize, isEmpty, getContentType, getOriginalFilename, getBytes.
     */
    private MultipartFile validImageMock(String contentType) throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1024L);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(contentType);
        when(file.getOriginalFilename()).thenReturn("test-image.jpg");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        return file;
    }

    /**
     * Mock para uploadAvatar: valida tamaño y tipo pero NO llama a convert().
     * Necesita: isEmpty, getSize, getContentType, getBytes (sin getOriginalFilename).
     */
    private MultipartFile validAvatarFileMock(String contentType) throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn(contentType);
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        return file;
    }

    /**
     * Mock para upload(MultipartFile, Map): no valida tipo/tamaño, solo llama a convert().
     * Necesita: getOriginalFilename, getBytes (sin getSize/isEmpty/getContentType).
     */
    private MultipartFile validOptionsFileMock() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test-image.jpg");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        return file;
    }
}
