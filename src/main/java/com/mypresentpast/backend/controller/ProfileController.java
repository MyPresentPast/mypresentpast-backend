package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.request.ProfileUpdateRequest;
import com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest;
import com.mypresentpast.backend.dto.response.ProfileResponse;
import com.mypresentpast.backend.dto.response.ProfileUpdateResponse;
import com.mypresentpast.backend.dto.response.UrlResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Actualiza los datos del perfil del usuario autenticado.
     * Permite modificar campos personales como nombre, apellido, email o nombre de usuario.
     *
     * @param request datos actualizados del perfil
     * @return perfil actualizado con los nuevos valores
     */
    @PatchMapping("/me")
    ResponseEntity<ProfileUpdateResponse> updateProfile(@RequestBody ProfileUpdateRequest request);

    /**
     * Cambia la contraseña del usuario autenticado.
     * Es necesario proporcionar la contraseña actual y la nueva para efectuar el cambio.
     *
     * @param request contiene la contraseña actual y la nueva
     * @return respuesta vacía con estado HTTP 204 si el cambio fue exitoso
     */
    @PutMapping("/me/password")
    ResponseEntity<Void> changeMyPassword(@Valid @RequestBody ChangePasswordRequest request);

    /**
     * Sube una nueva imagen de avatar para el usuario autenticado.
     * El archivo debe enviarse como multipart/form-data.
     *
     * @param file imagen a subir como nuevo avatar
     * @return URL pública del avatar subido
     */
    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<UrlResponse> uploadMyAvatar(@RequestPart("avatar") MultipartFile file);

}
