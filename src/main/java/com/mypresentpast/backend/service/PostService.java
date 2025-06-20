package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.ApiResponse;
import com.mypresentpast.backend.dto.CreatePostRequest;
import com.mypresentpast.backend.dto.MapResponse;
import com.mypresentpast.backend.dto.PostResponse;
import com.mypresentpast.backend.dto.UpdatePostRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio para operaciones relacionadas con Post.
 */
public interface PostService {

    /**
     * Crear una nueva publicación.
     *
     * @param request la solicitud de creación de publicación
     * @param images  archivos de imagen (opcional, máximo 5)
     * @return mensaje de éxito
     */
    ApiResponse createPost(CreatePostRequest request, List<MultipartFile> images);

    /**
     * Obtener publicación por ID.
     *
     * @param id el ID de la publicación
     * @return la respuesta de publicación
     */
    PostResponse getPostById(Long id);

    /**
     * Obtener datos para el mapa con filtros.
     * Devuelve todos los posts en el área especificada.
     *
     * @param latMin   latitud mínima del área visible del mapa
     * @param latMax   latitud máxima del área visible del mapa
     * @param lonMin   longitud mínima del área visible del mapa
     * @param lonMax   longitud máxima del área visible del mapa
     * @param category filtro por categoría (PostType)
     * @param date     fecha exacta del slider temporal
     * @return lista completa de posts en el área
     */
    MapResponse getMapData(double latMin, double latMax, double lonMin, double lonMax,
                           String category, LocalDate date);

    /**
     * Obtener una publicación aleatoria.
     *
     * @return una publicación aleatoria
     */
    PostResponse getRandomPost();

    /**
     * Corregir contenido de la publicación usando IA.
     *
     * @param postId el ID de la publicación
     * @return el contenido corregido
     */
    String correctPostContentWithAI(Long postId);

    /**
     * Actualizar una publicación existente.
     *
     * @param id        el ID de la publicación
     * @param request   los nuevos datos de la publicación
     * @param newImages nuevas imágenes a agregar (opcional)
     * @return mensaje de éxito
     */
    ApiResponse updatePost(Long id, UpdatePostRequest request, List<MultipartFile> newImages);

    /**
     * Eliminar una publicación (eliminación lógica).
     *
     * @param id el ID de la publicación
     * @return mensaje de éxito
     */
    ApiResponse deletePost(Long id);

    /**
     * Eliminar una imagen específica de una publicación.
     *
     * @param postId  el ID de la publicación
     * @param mediaId el ID de la imagen
     * @return mensaje de éxito
     */
    ApiResponse deletePostMedia(Long postId, Long mediaId);

    /**
     * Agregar nuevas imágenes a una publicación existente.
     *
     * @param postId    el ID de la publicación
     * @param imageUrls lista de URLs de imágenes
     * @return mensaje de éxito
     */
    ApiResponse addPostMedia(Long postId, List<String> imageUrls);
}