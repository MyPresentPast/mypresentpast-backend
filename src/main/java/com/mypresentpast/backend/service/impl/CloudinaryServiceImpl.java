package com.mypresentpast.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.CloudinaryException;
import com.mypresentpast.backend.service.CloudinaryService;
import com.mypresentpast.backend.utils.MessageBundle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Implementación del servicio de Cloudinary.
 */
@Slf4j
@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    
    private final Cloudinary cloudinary;

    // Constantes para compresión y validación
    private static final int MAX_FILE_SIZE_MB = 10; // 10MB máximo
    private static final int COMPRESSED_WIDTH = 1200; // Ancho máximo comprimido
    private static final int COMPRESSED_HEIGHT = 800; // Alto máximo comprimido
    private static final int QUALITY = 80; // Calidad de compresión (80%)


    public CloudinaryServiceImpl(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Override
    public Map<String, Object> upload(MultipartFile file) {
        try {
            // Validar tamaño del archivo (antes de procesar)
            if (file.getSize() > MAX_FILE_SIZE_MB * 1024 * 1024) {
                throw new IllegalArgumentException(
                    String.format("La imagen '%s' es demasiado grande (%.1f MB). Máximo permitido: %d MB", 
                        file.getOriginalFilename(), 
                        file.getSize() / (1024.0 * 1024.0), 
                        MAX_FILE_SIZE_MB)
                );
            }
            
            // Validar tipo de archivo
            if (!isValidImageType(file)) {
                throw new IllegalArgumentException(
                    String.format("Tipo de archivo no válido: '%s'. Formatos permitidos: JPG, PNG, GIF", 
                        file.getContentType())
                );
            }
            
            File convertedFile = convert(file);
            
            // Parámetros de subida CON COMPRESIÓN AUTOMÁTICA
            Map<String, Object> params = ObjectUtils.asMap(
                    "use_filename", true,
                    "unique_filename", true,
                    "overwrite", false,
                    // Compresión automática
                    "transformation", String.format("w_%d,h_%d,c_limit,q_%d,f_auto", 
                        COMPRESSED_WIDTH, COMPRESSED_HEIGHT, QUALITY)
            );
            
            Map<String, Object> result = cloudinary.uploader().upload(convertedFile, params);
            
            // Eliminar archivo temporal
            if (!convertedFile.delete()) {
            }
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Error al subir imagen: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> uploadAvatar(MultipartFile file, Long userId) {
        try {
            // Valida que el archivo sea una imagen válida antes de procesarlo
            validateImage(file);

            // Define el identificador público para la imagen, organizándola por ID de usuario
            // Esto permite sobreescribir fácilmente el avatar de un usuario específico
            String publicId = "avatars/%d/avatar".formatted(userId);

            // Construye los parámetros necesarios para la subida a Cloudinary
            // Se define la carpeta raíz, el nombre del archivo, y se aplican transformaciones de imagen
            Map<String, Object> params = ObjectUtils.asMap(
                    "public_id", publicId, // Nombre del archivo dentro de Cloudinary
                    "folder", "mypresentpast",     // Carpeta principal donde se guardan los archivos
                    "overwrite", true,             // Permite reemplazar el archivo si ya existe
                    "invalidate", true,            // Invalida caché en CDN si el archivo es reemplazado
                    "resource_type", "image",      // Especifica el tipo de recurso
                    "transformation", "w_400,h_400,c_fill,g_face,q_auto:good,f_auto" // Redimensiona y optimiza la imagen
            );
            // Realiza la carga del archivo en Cloudinary y devuelve el resultado como un mapa
            return cloudinary.uploader().upload(file.getBytes(), params);

        } catch (Exception e) {
            log.error("Error al subir imagen a Cloudinary para userId={}: {}", userId, e.getMessage(), e);
            // Captura cualquier error durante la carga y lanza una excepción específica con un mensaje predefinido
            throw new CloudinaryException(MessageBundle.CLOUDINARY_UPLOAD_ERROR, e);
        }
    }

    @Override
    public Map<String, Object> delete(String publicId) {
        try {

            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar imagen: " + e.getMessage());
        }
    }
    
    /**
     * Convierte MultipartFile a File temporal.
     */
    private File convert(MultipartFile file) throws IOException {
        File convertedFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        
        return convertedFile;
    }
    
    /**
     * Valida si el archivo es un tipo de imagen válido.
     */
    private boolean isValidImageType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp")
        );
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException(MessageBundle.AVATAR_FILE_REQUIRED);
        }
        if (file.getSize() > MAX_FILE_SIZE_MB * 1024 * 1024) {
            throw new BadRequestException(String.format(MessageBundle.AVATAR_FILE_TOO_LARGE, MAX_FILE_SIZE_MB));
        }
        String ct = file.getContentType();
        if (ct == null || !(ct.equals("image/jpeg") || ct.equals("image/png") || ct.equals("image/webp"))) {
            throw new BadRequestException(MessageBundle.AVATAR_FILE_INVALID_TYPE);
        }
    }

    @Override
    public Map<String, Object> upload(MultipartFile file, Map<String, Object> options) {
        log.info("Subiendo archivo con opciones personalizadas: {}", options);
        
        File tempFile = null;
        try {
            // Convertir MultipartFile a File temporal
            tempFile = convert(file);
            
            // Subir con opciones personalizadas
            Map<String, Object> result = cloudinary.uploader().upload(tempFile, options);
            
            log.info("Archivo subido exitosamente con public_id: {}", result.get("public_id"));
            return result;
            
        } catch (Exception e) {
            log.error("Error al subir archivo con opciones personalizadas", e);
            throw new CloudinaryException("Error al subir archivo: " + e.getMessage(), e);
        } finally {
            // Limpiar archivo temporal
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    log.warn("No se pudo eliminar el archivo temporal: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }
} 