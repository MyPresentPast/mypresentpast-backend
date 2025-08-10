package com.mypresentpast.backend.controller.impl;

import com.mypresentpast.backend.controller.ProfileController;
import com.mypresentpast.backend.dto.response.ProfileResponse;
import com.mypresentpast.backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementación del controlador de Profile.
 */
@RestController
@RequiredArgsConstructor
public class ProfileControllerImpl implements ProfileController {

    private final ProfileService profileService;

    @Override
    public ResponseEntity<ProfileResponse> getProfile(Long id) {
        ProfileResponse profile = profileService.getProfile(id);
        return ResponseEntity.ok(profile);
    }
}
