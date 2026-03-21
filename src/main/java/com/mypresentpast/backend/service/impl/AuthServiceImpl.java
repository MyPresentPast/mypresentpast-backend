package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.EmailRequest;
import com.mypresentpast.backend.dto.request.LoginRequest;
import com.mypresentpast.backend.dto.request.RegisterRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.AuthResponse;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.model.VerificationToken;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.repository.VerificationTokenRepository;
import com.mypresentpast.backend.service.AuthService;
import com.mypresentpast.backend.service.EmailService;
import com.mypresentpast.backend.service.JwtService;
import com.mypresentpast.backend.service.RecaptchaService;
import com.mypresentpast.backend.service.VerificationService;
import com.mypresentpast.backend.utils.CommonFunctions;
import com.mypresentpast.backend.utils.MessageBundle;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepository verificationTokenRepository;
    private final VerificationService verificationService;
    private final EmailService emailService;
    private final RecaptchaService recaptchaService;

    /**
     * Autentica al usuario usando su email y contraseña.
     * Si la autenticación es exitosa, genera y devuelve un JWT.
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        if (!recaptchaService.verify(request.getRecaptchaToken())) {
            throw new BadRequestException("Verificación reCAPTCHA fallida. Por favor, intentá de nuevo.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(MessageBundle.LOGIN_FAILED));

        // verificar la contraseña primero
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException(MessageBundle.LOGIN_FAILED);
        }

        // verificar si el email está confirmado
        if (!user.isEmailVerified()) {
            throw new DisabledException(MessageBundle.USER_DISABLED);
        }

        String token = jwtService.getToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }


    /**
     * Registra un nuevo usuario en el sistema con rol NORMAL.
     * Verifica que el email y el profileUsername no estén duplicados.
     */
    @Override
    public ApiResponse register(RegisterRequest request) throws MessagingException {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(MessageBundle.PASSWORD_MISMATCH);
        }

        if (!isValidPassword(request.getPassword())) {
            throw new BadRequestException(MessageBundle.PASSWORD_INVALID);
        }

        // Verifica que el profile username no este registrado
        if (userRepository.existsByProfileUsername(request.getProfileUsername())) {
            throw new DataIntegrityViolationException(String.format(MessageBundle.DUPLICATE_USERNAME, request.getProfileUsername()));
        }

        Optional<User> existingUserOpt = userRepository.findByEmail(request.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (existingUser.isEmailVerified()) {
                // Caso 1: usuario ya verificado → no se puede registrar de nuevo
                throw new DataIntegrityViolationException(String.format(MessageBundle.DUPLICATE_EMAIL, request.getEmail()));
            } else {
                // Caso 2: usuario no verificado
                VerificationToken vToken = verificationTokenRepository.findByUser(existingUser)
                        .orElse(null);

                if (vToken != null && vToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                    // Token sigue siendo válido → no permitimos re-registro
                    throw new BadRequestException("Ya existe un registro pendiente para este email. Revisa tu correo.");
                }

                // Token vencido → eliminamos usuario y token viejo
                verificationTokenRepository.deleteByUser(existingUser);
                userRepository.delete(existingUser);
            }
        }

        // Crea el nuevo usuario con los datos del request
        User user = User
                .builder()
                .profileUsername(request.getProfileUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(UserRole.NORMAL)
                .name(CommonFunctions.formatAsTitleCase(request.getName()))
                .lastName(CommonFunctions.formatAsTitleCase(request.getLastName()))
                .build();

        // Guarda el usuario en la base de datos
        userRepository.save(user);

        // Creamos el token de verificación
        VerificationToken verificationToken = verificationService.createVerificationToken(user);

        // Enviar email
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRecipient(user.getEmail());
        emailRequest.setSubject("Confirma tu email en MyPresentPast");
        emailRequest.setName(user.getName() + " " + user.getLastName());
        emailRequest.setVerificationUrl("http://localhost:4200/verify-success?token=" + verificationToken.getToken());

        emailService.sendMail(emailRequest);


        // Devuelve la respuesta con el mensaje y el token de verificación (solo para pruebas)
        return ApiResponse
                .builder()
                .message("Usuario creado. Revisa tu correo para confirmar tu cuenta. Token: " + verificationToken.getToken())
                .build();
    }

    private boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }

}
