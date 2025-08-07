package com.mypresentpast.backend.utils;

import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utilidades para manejar la seguridad y obtener el usuario autenticado.
 */
public class SecurityUtils {

    /**
     * Obtiene el usuario autenticado actualmente.
     * 
     * @return el usuario autenticado
     * @throws UnauthorizedException si no hay usuario autenticado
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No hay usuario autenticado");
        }

        Object principal = authentication.getPrincipal();
        
        if (!(principal instanceof UserDetails)) {
            throw new UnauthorizedException("Principal no es un UserDetails válido");
        }

        if (!(principal instanceof User)) {
            throw new UnauthorizedException("Principal no es un User válido");
        }

        return (User) principal;
    }

    /**
     * Obtiene el ID del usuario autenticado actualmente.
     * 
     * @return el ID del usuario autenticado
     * @throws UnauthorizedException si no hay usuario autenticado
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}