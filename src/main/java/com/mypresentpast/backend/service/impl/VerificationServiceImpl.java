package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.VerificationToken;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.repository.VerificationTokenRepository;
import com.mypresentpast.backend.service.VerificationService;
import com.mypresentpast.backend.utils.MessageBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    private static final long EXPIRATION_HOURS = 24;


    @Override
    public VerificationToken createVerificationToken(User user) {
        // si ya existía un token viejo, lo borramos
        tokenRepository.deleteByUser(user);

        // creamos un nuevo token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS));

        return tokenRepository.save(verificationToken);
    }

    @Override
    public ApiResponse validateVerificationToken(String token) {
        // busca el token
        VerificationToken vToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(MessageBundle.TOKEN_NOT_FOUND));
        // valida si no esta vencido
        if (vToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(MessageBundle.TOKEN_EXPIRED);
        }
        // actualiza el usuario para que el email este verificado y elimina el token
        User user = vToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // El token ya no sirve más
        tokenRepository.delete(vToken);

        return ApiResponse.builder()
                .message("Email confirmado con éxito")
                .build();
    }

}
