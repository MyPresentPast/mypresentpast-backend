package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.response.ProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para operaciones relacionadas con perfiles de usuario.
 */
@RequestMapping("/profiles")
public interface ProfileController {

    /**
     * Obtener perfil de usuario por ID.
     * Devuelve el perfil público si es otro usuario, o el perfil completo si es el propio.
     * 
     * @param id ID del usuario
     * @return información del perfil con estadísticas de seguimiento
     */
    @GetMapping("/{id}")
    ResponseEntity<ProfileResponse> getProfile(@PathVariable Long id);
}
