package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.request.CreateCollectionAndSavePostRequest;
import com.mypresentpast.backend.dto.request.CreateCollectionRequest;
import com.mypresentpast.backend.dto.request.UpdateCollectionRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.CollectionSummaryResponse;
import com.mypresentpast.backend.dto.response.PostCollectionStatusResponse;
import com.mypresentpast.backend.dto.response.PostResponse;
import java.util.List;

/**
 * Servicio para operaciones relacionadas con Collection.
 */
public interface CollectionService {

    /**
     * Obtener las colecciones del usuario autenticado.
     *
     * @return lista de colecciones con resumen
     */
    List<CollectionSummaryResponse> getMyCollections();

    /**
     * Crear nueva colección.
     *
     * @param request datos de la colección
     * @return mensaje de éxito
     */
    ApiResponse createCollection(CreateCollectionRequest request);

    /**
     * Actualizar colección existente.
     *
     * @param id      ID de la colección
     * @param request nuevos datos de la colección
     * @return mensaje de éxito
     */
    ApiResponse updateCollection(Long id, UpdateCollectionRequest request);

    /**
     * Eliminar colección.
     *
     * @param id ID de la colección
     */
    void deleteCollection(Long id);

    /**
     * Obtener posts de una colección.
     *
     * @param id ID de la colección
     * @return lista de posts en la colección
     */
    List<PostResponse> getCollectionPosts(Long id);

    /**
     * Obtener estado de un post en las colecciones del usuario.
     *
     * @param postId ID del post
     * @return estado del post en colecciones
     */
    PostCollectionStatusResponse getPostCollectionStatus(Long postId);

    /**
     * Agregar post a colección.
     *
     * @param collectionId ID de la colección
     * @param postId       ID del post
     * @return mensaje de éxito
     */
    ApiResponse addPostToCollection(Long collectionId, Long postId);

    /**
     * Quitar post de colección.
     *
     * @param collectionId ID de la colección
     * @param postId       ID del post
     * @return mensaje de éxito
     */
    ApiResponse removePostFromCollection(Long collectionId, Long postId);

    /**
     * Crear colección y guardar post en un solo paso.
     *
     * @param request datos de la colección y post
     * @return mensaje de éxito
     */
    ApiResponse createCollectionAndSavePost(CreateCollectionAndSavePostRequest request);
}
