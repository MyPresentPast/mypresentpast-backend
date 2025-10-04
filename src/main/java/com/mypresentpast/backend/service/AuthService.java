package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.request.LoginRequest;
import com.mypresentpast.backend.dto.request.RegisterRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.AuthResponse;
import jakarta.mail.MessagingException;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    ApiResponse register(RegisterRequest request) throws MessagingException;
}
