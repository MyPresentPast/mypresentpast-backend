package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.FollowStatsResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para operaciones relacionadas con Follow.
 */
@RequestMapping("/follow")
public interface FollowController {

    /**
     * Seguir a un usuario.
     * 
     * @param followeeId ID del usuario a seguir
     * @return mensaje de éxito
     */
    @PostMapping("/{followeeId}")
    ResponseEntity<ApiResponse> follow(@PathVariable Long followeeId);

    /**
     * Dejar de seguir a un usuario.
     * 
     * @param followeeId ID del usuario a dejar de seguir
     * @return mensaje de éxito
     */
    @DeleteMapping("/{followeeId}")
    ResponseEntity<ApiResponse> unfollow(@PathVariable Long followeeId);

    /**
     * Obtener la lista de usuarios que sigue el usuario actual.
     * Solo para el perfil propio - para mostrar en el modal de "Seguidos".
     * 
     * @return lista de usuarios seguidos por el usuario actual
     */
    @GetMapping("/my-following")
    ResponseEntity<List<UserDto>> getMyFollowing();

    /**
     * Obtener la lista de seguidores del usuario actual.
     * Solo para el perfil propio - para mostrar en el modal de "Seguidores".
     * 
     * @return lista de usuarios que siguen al usuario actual
     */
    @GetMapping("/my-followers")
    ResponseEntity<List<UserDto>> getMyFollowers();

    /**
     * Obtener la lista de usuarios que sigue un usuario específico.
     *
     * @param userId identificador del usuario
     * @return lista de usuarios seguidos por el usuario indicado
     */
    @GetMapping("/{userId}/following")
    ResponseEntity<List<UserDto>> getFollowingByUserId(@PathVariable Long userId);

    /**
     * Obtener la lista de seguidores de un usuario específico.
     *
     * @param userId identificador del usuario
     * @return lista de usuarios que siguen al usuario indicado
     */
    @GetMapping("/{userId}/followers")
    ResponseEntity<List<UserDto>> getFollowersByUserId(@PathVariable Long userId);


    /**
     * Verificar si el usuario actual sigue a otro usuario.
     * 
     * @param userId ID del usuario a verificar
     * @return true si lo sigue, false si no
     */
    @GetMapping("/status/{userId}")
    ResponseEntity<Boolean> isFollowing(@PathVariable Long userId);

    /**
     * Obtener estadísticas de seguimiento de un usuario.
     * 
     * @param userId ID del usuario
     * @return estadísticas de seguimiento
     */
    @GetMapping("/stats/{userId}")
    ResponseEntity<FollowStatsResponse> getFollowStats(@PathVariable Long userId);
}
