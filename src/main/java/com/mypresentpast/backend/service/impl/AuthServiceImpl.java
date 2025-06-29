package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.LoginRequest;
import com.mypresentpast.backend.dto.request.RegisterRequest;
import com.mypresentpast.backend.dto.response.AuthResponse;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.AuthService;
import com.mypresentpast.backend.service.JwtService;
import com.mypresentpast.backend.utils.MessageBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.getToken(user);
        return AuthResponse
                .builder()
                .token(token)
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException(String.format(MessageBundle.DUPLICATE_EMAIL, request.getEmail()));
        }

        if (userRepository.existsByProfileUsername(request.getProfileUsername())) {
            throw new DataIntegrityViolationException(String.format(MessageBundle.DUPLICATE_USERNAME, request.getProfileUsername()));
        }

        User user = User
                .builder()
                .profileUsername(request.getProfileUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(UserRole.NORMAL)
                .build();

        userRepository.save(user);
        return AuthResponse
                .builder()
                .token(jwtService.getToken(user))
                .build();
    }
}
