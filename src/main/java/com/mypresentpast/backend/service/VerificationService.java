package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.VerificationToken;

public interface VerificationService {
    VerificationToken createVerificationToken(User user);
    ApiResponse validateVerificationToken(String token);
}
