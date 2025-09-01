package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.FollowStatsResponse;
import java.util.List;

/**
 * Servicio para manejar operaciones de seguimiento entre usuarios.
 */
public interface FollowService {
    
    /**
     * Seguir a un usuario.
     * 
     * @param followeeId ID del usuario a seguir
     * @return respuesta con mensaje de éxito
     */
    ApiResponse follow(Long followeeId);

    /**
     * Dejar de seguir a un usuario.
     * 
     * @param followeeId ID del usuario a dejar de seguir
     * @return respuesta con mensaje de éxito
     */
    ApiResponse unfollow(Long followeeId);

    /**
     * Obtener lista de usuarios que sigue el usuario actual.
     * Solo para el perfil propio.
     * 
     * @return lista de usuarios seguidos por el usuario actual
     */
    List<UserDto> getMyFollowing();

    /**
     * Obtener lista de seguidores del usuario actual.
     * Solo para el perfil propio.
     * 
     * @return lista de usuarios que siguen al usuario actual
     */
    List<UserDto> getMyFollowers();


    /**
     * Obtener los usuarios que sigue un usuario específico.
     *
     * @param userId identificador del usuario
     */
    List<UserDto> getFollowingByUserId(Long userId);

    /**
     * Obtener los seguidores de un usuario específico.
     *
     * @param userId identificador del usuario
     */
    List<UserDto> getFollowersByUserId(Long userId);


    /**
     * Verificar si el usuario actual sigue a otro usuario.
     * 
     * @param userId ID del usuario a verificar
     * @return true si lo sigue, false si no
     */
    Boolean isFollowing(Long userId);

    /**
     * Obtener estadísticas de seguimiento de un usuario.
     * 
     * @param userId ID del usuario
     * @return estadísticas de seguimiento
     */
    FollowStatsResponse getFollowStats(Long userId);
}
