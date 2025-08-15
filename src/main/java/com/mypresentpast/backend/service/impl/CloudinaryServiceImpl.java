package com.mypresentpast.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mypresentpast.backend.service.CloudinaryService;
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
@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    
    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;
    
    // Constantes para compresión y validación
    private static final int MAX_FILE_SIZE_MB = 10; // 10MB máximo
    private static final int COMPRESSED_WIDTH = 1200; // Ancho máximo comprimido
    private static final int COMPRESSED_HEIGHT = 800; // Alto máximo comprimido
    private static final int QUALITY = 80; // Calidad de compresión (80%)


    
    public CloudinaryServiceImpl() {
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
            validateImage(file);

            String publicId = "avatars/%d/avatar".formatted(userId);

            Map<String, Object> params = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "mypresentpast",
                    "overwrite", true,
                    "invalidate", true,
                    "resource_type", "image",
                    "transformation", "w_400,h_400,c_fill,g_face,q_auto:good,f_auto"
            );

            return cloudinary.uploader().upload(file.getBytes(), params);

        } catch (Exception e) {
            throw new RuntimeException("Error al subir avatar: " + e.getMessage());
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
            throw new IllegalArgumentException("El archivo es obligatorio");
        }
        if (file.getSize() > MAX_FILE_SIZE_MB * 1024 * 1024) {
            throw new IllegalArgumentException("La imagen excede el tamaño permitido");
        }
        String ct = file.getContentType();
        if (ct == null || !(ct.equals("image/jpeg") || ct.equals("image/png") || ct.equals("image/webp"))) {
            throw new IllegalArgumentException("Formato no permitido para avatar");
        }
    }
} 