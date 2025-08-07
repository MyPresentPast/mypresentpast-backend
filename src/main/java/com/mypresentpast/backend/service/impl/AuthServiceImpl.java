package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.LoginRequest;
import com.mypresentpast.backend.dto.request.RegisterRequest;
import com.mypresentpast.backend.dto.response.AuthResponse;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.AuthService;
import com.mypresentpast.backend.service.JwtService;
import com.mypresentpast.backend.utils.CommonFunctions;
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

    /**
     * Autentica al usuario usando su email y contraseña.
     * Si la autenticación es exitosa, genera y devuelve un JWT.
     */
    @Override
    public AuthResponse login(LoginRequest request) {

        // Verifica las credenciales del usuario (lanzará una excepción si son incorrectas)
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Recupera el usuario desde el repositorio
        UserDetails user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        // Genera el token JWT
        String token = jwtService.getToken(user);

        // Devuelve la respuesta con el token
        return AuthResponse
                .builder()
                .token(token)
                .build();
    }

    /**
     * Registra un nuevo usuario en el sistema con rol NORMAL.
     * Verifica que el email y el profileUsername no estén duplicados.
     */
    @Override
    public AuthResponse register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(MessageBundle.PASSWORD_MISMATCH);
        }

        if (!isValidPassword(request.getPassword())) {
            throw new BadRequestException(MessageBundle.PASSWORD_INVALID);
        }

        // Verifica que el email y el profile username no esten registrados
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException(String.format(MessageBundle.DUPLICATE_EMAIL, request.getEmail()));
        }

        if (userRepository.existsByProfileUsername(request.getProfileUsername())) {
            throw new DataIntegrityViolationException(String.format(MessageBundle.DUPLICATE_USERNAME, request.getProfileUsername()));
        }

        // Formatea nombre y apellido en formato "Nombre Apellido"
        String capitalizedName = CommonFunctions.formatAsTitleCase(request.getName());
        String capitalizedLastName = CommonFunctions.formatAsTitleCase(request.getLastName());

        // Crea el nuevo usuario con los datos del request
        User user = User
                .builder()
                .profileUsername(request.getProfileUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(UserRole.NORMAL)
                .name(capitalizedName)
                .lastName(capitalizedLastName)
                .build();

        // Guarda el usuario en la base de datos
        userRepository.save(user);

        // Devuelve la respuesta con el token JWT generado
        return AuthResponse
                .builder()
                .token(jwtService.getToken(user))
                .build();
    }

    private boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }

}
