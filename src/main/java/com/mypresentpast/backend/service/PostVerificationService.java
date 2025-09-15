package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.response.ApiResponse;

/**
 * Servicio para manejar la verificación de posts por parte de instituciones.
 */
public interface PostVerificationService {

    /**
     * Verifica un post. Solo pueden verificar usuarios con rol INSTITUTION.
     * Restricciones:
     * - Solo posts con estado ACTIVE
     * - No pueden verificar sus propios posts (ya están auto-verificados)
     * - Solo puede haber una verificación activa por post
     *
     * @param postId ID del post a verificar
     * @return ApiResponse con resultado de la operación
     */
    ApiResponse verifyPost(Long postId);

    /**
     * Desverifica un post. Solo puede desverificar quien lo verificó originalmente.
     * 
     * @param postId ID del post a desverificar
     * @return ApiResponse con resultado de la operación
     */
    ApiResponse unverifyPost(Long postId);
}
