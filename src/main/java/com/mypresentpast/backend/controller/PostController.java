package com.mypresentpast.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.MapResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para operaciones relacionadas con Post.
 */
@RequestMapping("/posts")
public interface PostController {

    /**
     * Crear una nueva publicación.
     *
     * @param images  archivos de imagen (opcional, máximo 5)
     * @return mensaje de éxito "Publicación creada con éxito"
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> createPost(
        @RequestParam("data") String data,
        @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) throws JsonProcessingException;

    /**
     * Obtener una publicación por ID.
     *
     * @param id el ID de la publicación
     * @return la publicación encontrada
     */
    @GetMapping("/{id}")
    ResponseEntity<PostResponse> getPostById(@PathVariable Long id);

    /**
     * Obtener publicaciones por usuario.
     *
     * @param id el ID del usuario
     * @return las publicaciones encontradas
     */
    @GetMapping("/user/{id}")
    ResponseEntity<List<PostResponse>> getPostsByUser(@PathVariable Long id);

    /**
     * Endpoint principal del mapa con filtros.
     * Devuelve todos los posts en el área especificada.
     *
     * @param latMin     latitud mínima del área visible del mapa
     * @param latMax     latitud máxima del área visible del mapa
     * @param lonMin     longitud mínima del área visible del mapa
     * @param lonMax     longitud máxima del área visible del mapa
     * @param category   filtro por categoría (PostType: STORY, INFORMATION, MYTH)
     * @param date       fecha exacta del slider temporal (ej: "2006-07-09")
     * @param isVerified filtro por publicaciones verificadas (true/false)
     * @param isByIA     filtro por publicaciones creadas con IA (true/false)
     * @return lista completa de posts en el área
     */
    @GetMapping("/map")
    ResponseEntity<MapResponse> getMapData(
        @RequestParam double latMin,
        @RequestParam double latMax,
        @RequestParam double lonMin,
        @RequestParam double lonMax,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(required = false) Boolean isVerified,
        @RequestParam(required = false) Boolean isByIA
    );

    /**
     * Obtener una publicación aleatoria para la funcionalidad "¡Haceme volar al infinito!".
     *
     * @return una publicación aleatoria
     */
    @GetMapping("/random")
    ResponseEntity<PostResponse> getRandomPost();



    /**
     * Actualizar una publicación existente.
     *
     * @param id        el ID de la publicación a actualizar
     * @param newImages nuevas imágenes a agregar (opcional)
     * @return mensaje de éxito
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> updatePost(
        @PathVariable Long id,
        @RequestParam("data") String data,
        @RequestParam(value = "newImages", required = false) List<MultipartFile> newImages
    ) throws JsonProcessingException;

    /**
     * Eliminar una publicación (eliminación lógica).
     *
     * @param id el ID de la publicación a eliminar
     * @return mensaje de éxito
     */
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse> deletePost(@PathVariable Long id);
}
