package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.response.ProfileResponse;

/**
 * Servicio para manejar operaciones relacionadas con perfiles de usuario.
 */
public interface ProfileService {
    
    /**
     * Obtener perfil de usuario por ID.
     * Devuelve el perfil con toda la información necesaria para mostrar 
     * tanto perfiles propios como ajenos.
     * 
     * @param userId ID del usuario del perfil a obtener
     * @return información completa del perfil con estadísticas
     */
    ProfileResponse getProfile(Long userId);
}
