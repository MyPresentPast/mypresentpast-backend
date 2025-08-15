package com.mypresentpast.backend.service;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio para operaciones con Cloudinary.
 */
public interface CloudinaryService {

    /**
     * Subir imagen a Cloudinary.
     *
     * @param file archivo de imagen
     * @return mapa con información de la imagen subida
     */
    Map<String, Object> upload(MultipartFile file);

    /**
     * Eliminar imagen de Cloudinary.
     *
     * @param publicId ID público de la imagen en Cloudinary
     * @return mapa con resultado de la eliminación
     */
    Map<String, Object> delete(String publicId);

    Map<String, Object> uploadAvatar(MultipartFile file, Long userId);

} 