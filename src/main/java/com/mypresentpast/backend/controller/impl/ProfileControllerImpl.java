package com.mypresentpast.backend.controller.impl;

import com.mypresentpast.backend.controller.ProfileController;
import com.mypresentpast.backend.dto.request.ProfileUpdateRequest;
import com.mypresentpast.backend.dto.request.profile.ChangePasswordRequest;
import com.mypresentpast.backend.dto.response.ProfileResponse;
import com.mypresentpast.backend.dto.response.ProfileUpdateResponse;
import com.mypresentpast.backend.dto.response.UrlResponse;
import com.mypresentpast.backend.service.ProfileService;
import com.mypresentpast.backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    public ResponseEntity<ProfileUpdateResponse> updateProfile(ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(request));
    }

    @Override
    public ResponseEntity<Void> changeMyPassword(ChangePasswordRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        profileService.changePassword(currentUserId, request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UrlResponse> uploadMyAvatar(@RequestPart("avatar") MultipartFile file) {
        String url = profileService.uploadAvatar(file);
        return ResponseEntity.ok(new UrlResponse(url));
    }
}
