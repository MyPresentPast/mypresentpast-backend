package com.mypresentpast.backend.controller.auth;

import com.mypresentpast.backend.dto.request.LoginRequest;
import com.mypresentpast.backend.dto.request.RegisterRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.AuthResponse;
import com.mypresentpast.backend.service.AuthService;
import com.mypresentpast.backend.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationService verificationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(verificationService.validateVerificationToken(token));
    }

}
