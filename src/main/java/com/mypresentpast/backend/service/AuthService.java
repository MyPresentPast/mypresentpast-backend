package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.request.LoginRequest;
import com.mypresentpast.backend.dto.request.RegisterRequest;
import com.mypresentpast.backend.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);
}
