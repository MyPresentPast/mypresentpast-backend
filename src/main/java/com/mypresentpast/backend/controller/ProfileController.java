package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest;
import com.mypresentpast.backend.dto.response.ProfileResponse;
import com.mypresentpast.backend.dto.request.ProfileUpdateRequest;
import com.mypresentpast.backend.dto.response.ProfileUpdateResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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

    @PatchMapping("/me")
    ResponseEntity<ProfileUpdateResponse> updateProfile(@RequestBody ProfileUpdateRequest request);

    @PutMapping("/me/password")
    ResponseEntity<Void> changeMyPassword(@Valid @RequestBody ChangePasswordRequest request);

    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Map<String, String>> uploadMyAvatar(@RequestPart("file") MultipartFile file);

}
