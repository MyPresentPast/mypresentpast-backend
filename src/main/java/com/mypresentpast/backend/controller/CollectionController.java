package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.request.CreateCollectionAndSavePostRequest;
import com.mypresentpast.backend.dto.request.CreateCollectionRequest;
import com.mypresentpast.backend.dto.request.UpdateCollectionRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.CollectionSummaryResponse;
import com.mypresentpast.backend.dto.response.PostCollectionStatusResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para operaciones relacionadas con Collection.
 */
@RequestMapping("/collections")
public interface CollectionController {

    /**
     * Obtener mis colecciones.
     *
     * @return lista de colecciones del usuario autenticado
     */
    @GetMapping("/my")
    ResponseEntity<List<CollectionSummaryResponse>> getMyCollections();

    /**
     * Crear nueva colección.
     *
     * @param request datos de la colección
     * @return mensaje de éxito
     */
    @PostMapping
    ResponseEntity<ApiResponse> createCollection(@Valid @RequestBody CreateCollectionRequest request);

    /**
     * Actualizar colección existente.
     *
     * @param id      ID de la colección
     * @param request nuevos datos de la colección
     * @return mensaje de éxito
     */
    @PutMapping("/{id}")
    ResponseEntity<ApiResponse> updateCollection(@PathVariable Long id, 
                                                 @Valid @RequestBody UpdateCollectionRequest request);

    /**
     * Eliminar colección.
     *
     * @param id ID de la colección
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCollection(@PathVariable Long id);

    /**
     * Obtener posts de una colección.
     *
     * @param id ID de la colección
     * @return lista de posts en la colección
     */
    @GetMapping("/{id}/posts")
    ResponseEntity<List<PostResponse>> getCollectionPosts(@PathVariable Long id);

    /**
     * Obtener estado de un post en mis colecciones.
     *
     * @param postId ID del post
     * @return estado del post en colecciones
     */
    @GetMapping("/my/posts/{postId}/status")
    ResponseEntity<PostCollectionStatusResponse> getPostCollectionStatus(@PathVariable Long postId);

    /**
     * Agregar post a colección.
     *
     * @param collectionId ID de la colección
     * @param postId       ID del post
     * @return mensaje de éxito
     */
    @PostMapping("/{collectionId}/posts/{postId}")
    ResponseEntity<ApiResponse> addPostToCollection(@PathVariable Long collectionId, 
                                                    @PathVariable Long postId);

    /**
     * Quitar post de colección.
     *
     * @param collectionId ID de la colección
     * @param postId       ID del post
     * @return mensaje de éxito
     */
    @DeleteMapping("/{collectionId}/posts/{postId}")
    ResponseEntity<ApiResponse> removePostFromCollection(@PathVariable Long collectionId, 
                                                         @PathVariable Long postId);

    /**
     * Crear colección y guardar post en un solo paso.
     *
     * @param request datos de la colección y post
     * @return mensaje de éxito
     */
    @PostMapping("/create-and-save")
    ResponseEntity<ApiResponse> createCollectionAndSavePost(@Valid @RequestBody CreateCollectionAndSavePostRequest request);
}
