package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.ApiResponse;
import com.mypresentpast.backend.dto.MediaDto;
import com.mypresentpast.backend.model.Media;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio para operaciones con Media.
 */
public interface MediaService {

    /**
     * Listar todas las imágenes disponibles (sin post asignado).
     *
     * @return lista de imágenes
     */
    List<MediaDto> listAvailableMedia();

    /**
     * Subir imagen a Cloudinary y guardar en BD.
     *
     * @param file archivo de imagen
     * @return información de la imagen subida
     */
    MediaDto uploadImage(MultipartFile file);

    /**
     * Eliminar imagen de Cloudinary y BD.
     *
     * @param mediaId ID de la imagen
     * @return respuesta de éxito
     */
    ApiResponse deleteImage(Long mediaId);

    /**
     * Obtener imagen por ID.
     *
     * @param id ID de la imagen
     * @return imagen encontrada
     */
    Media getById(Long id);

    /**
     * Verificar si existe imagen por ID.
     *
     * @param id ID de la imagen
     * @return true si existe
     */
    boolean existsById(Long id);
} 