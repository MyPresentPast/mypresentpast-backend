package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

// JwtServiceImpl no tiene dependencias mockeables: sus campos @Value (secretKey y expirationTimeMs)
// se inyectan via reflexión en setUp(). Los tokens se generan con el propio servicio en los tests
// que requieren un token válido, garantizando coherencia entre generación y validación.
@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    // Clave Base64 de 256 bits válida para HS256
    private static final String TEST_SECRET_KEY =
            "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLUhTMjU2";

    // 1 hora en milisegundos
    private static final long TEST_EXPIRATION_MS = 3_600_000L;

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtServiceImpl();

        Field secretKeyField = JwtServiceImpl.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtService, TEST_SECRET_KEY);

        Field expirationField = JwtServiceImpl.class.getDeclaredField("expirationTimeMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, TEST_EXPIRATION_MS);
    }

    // ── getToken(UserDetails) ──────────────────────────────────────────────

    // Un JWT válido tiene exactamente tres partes separadas por punto (header.payload.signature).
    @Test
    void getToken_ValidUser_ReturnsTokenWithThreeParts() {
        User user = validUserMock();

        String token = jwtService.getToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }

    // El subject del token debe ser el email del usuario (username de Spring Security).
    @Test
    void getToken_ValidUser_SubjectIsUserEmail() {
        User user = validUserMock();

        String token = jwtService.getToken(user);

        assertEquals("john@example.com", jwtService.getUsernameFromToken(token));
    }

    // El token debe contener rol, uid, name, lastName y profileUsername del usuario.
    @Test
    void getToken_ValidUser_TokenContainsExpectedClaims() {
        User user = validUserMock();

        String token = jwtService.getToken(user);
        Claims claims = jwtService.getClaim(token, c -> c);

        assertEquals("NORMAL", claims.get("role", String.class));
        assertEquals("John", claims.get("name", String.class));
        assertEquals("Doe", claims.get("lastName", String.class));
        assertEquals("johndoe", claims.get("profileUsername", String.class));
    }

    // La fecha de expiración del token debe quedar en el futuro respecto al momento de generación.
    @Test
    void getToken_ValidUser_ExpirationIsInTheFuture() {
        User user = validUserMock();

        String token = jwtService.getToken(user);
        Date expiration = jwtService.getClaim(token, Claims::getExpiration);

        assertTrue(expiration.after(new Date()));
    }

    // Pasar un UserDetails que no sea instancia de User debe lanzar IllegalArgumentException.
    @Test
    void getToken_NonUserInstance_ThrowsIllegalArgumentException() {
        UserDetails genericUser = mock(UserDetails.class);

        assertThrows(IllegalArgumentException.class, () -> jwtService.getToken(genericUser));
    }

    // ── getUsernameFromToken(String) ───────────────────────────────────────

    // Extraer el username de un token válido debe retornar el email del usuario.
    @Test
    void getUsernameFromToken_ValidToken_ReturnsEmail() {
        User user = validUserMock();
        String token = jwtService.getToken(user);

        String username = jwtService.getUsernameFromToken(token);

        assertEquals("john@example.com", username);
    }

    // Un token con el último carácter de la firma modificado debe lanzar excepción de JWT.
    @Test
    void getUsernameFromToken_TamperedSignature_ThrowsJwtException() {
        User user = validUserMock();
        String token = jwtService.getToken(user);
        String tampered = token.substring(0, token.length() - 1) + "X";

        assertThrows(Exception.class, () -> jwtService.getUsernameFromToken(tampered));
    }

    // Una cadena que no es un JWT válido debe lanzar excepción al intentar parsear.
    @Test
    void getUsernameFromToken_MalformedToken_ThrowsJwtException() {
        assertThrows(Exception.class, () -> jwtService.getUsernameFromToken("not.a.jwt"));
    }

    // Un token ya expirado debe lanzar ExpiredJwtException al intentar extraer el username.
    @Test
    void getUsernameFromToken_ExpiredToken_ThrowsExpiredJwtException() {
        String expiredToken = buildExpiredToken("expired@example.com");

        assertThrows(ExpiredJwtException.class, () -> jwtService.getUsernameFromToken(expiredToken));
    }

    // ── isTokenValid(String, UserDetails) ─────────────────────────────────

    // Un token generado para el usuario debe ser válido cuando el email coincide y no expiró.
    @Test
    void isTokenValid_MatchingUserAndFreshToken_ReturnsTrue() {
        User user = validUserMock();
        String token = jwtService.getToken(user);

        boolean valid = jwtService.isTokenValid(token, user);

        assertTrue(valid);
    }

    // Un token generado para un usuario no debe ser válido contra otro email distinto.
    @Test
    void isTokenValid_DifferentUserEmail_ReturnsFalse() {
        User user = validUserMock();
        String token = jwtService.getToken(user);

        User otherUser = User.builder()
                .id(99L)
                .email("other@example.com")
                .profileUsername("other")
                .name("Other")
                .lastName("User")
                .role(UserRole.NORMAL)
                .emailVerified(true)
                .password("pass")
                .build();

        boolean valid = jwtService.isTokenValid(token, otherUser);

        assertFalse(valid);
    }

    // Validar un token expirado lanza ExpiredJwtException porque jjwt falla al parsear antes de comparar.
    @Test
    void isTokenValid_ExpiredToken_ThrowsExpiredJwtException() {
        User user = validUserMock();
        String expiredToken = buildExpiredToken(user.getEmail());

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, user));
    }

    // ── getClaim(String, Function<Claims, T>) ─────────────────────────────

    // El claim iat del token debe ser una fecha reciente (dentro de los últimos 5 segundos).
    @Test
    void getClaim_IssuedAtClaim_ReturnsDateCloseToNow() {
        User user = validUserMock();
        String token = jwtService.getToken(user);

        Date issuedAt = jwtService.getClaim(token, Claims::getIssuedAt);

        assertNotNull(issuedAt);
        long diffMs = System.currentTimeMillis() - issuedAt.getTime();
        assertTrue(diffMs >= 0 && diffMs < 5_000L);
    }

    // El claim "role" debe retornar el nombre del rol del usuario como String.
    @Test
    void getClaim_RoleClaim_ReturnsRoleString() {
        User user = validUserMock();
        String token = jwtService.getToken(user);

        String role = jwtService.getClaim(token, claims -> claims.get("role", String.class));

        assertEquals("NORMAL", role);
    }

    // El claim "profileUsername" debe retornar el alias visible del usuario.
    @Test
    void getClaim_ProfileUsernameClaim_ReturnsProfileUsername() {
        User user = validUserMock();
        String token = jwtService.getToken(user);

        String profileUsername = jwtService.getClaim(token, claims -> claims.get("profileUsername", String.class));

        assertEquals("johndoe", profileUsername);
    }

    // Un token firmado con una clave distinta no puede ser parseado con la clave del servicio.
    @Test
    void getClaim_TokenSignedWithDifferentKey_ThrowsJwtException() {
        String tokenWithOtherKey = buildTokenWithDifferentKey("other@example.com");

        assertThrows(Exception.class, () -> jwtService.getClaim(tokenWithOtherKey, Claims::getSubject));
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * Construye un User completamente poblado con valores fijos para usar en los tests.
     */
    private User validUserMock() {
        return User.builder()
                .id(42L)
                .email("john@example.com")
                .profileUsername("johndoe")
                .name("John")
                .lastName("Doe")
                .role(UserRole.NORMAL)
                .emailVerified(true)
                .password("hashedpassword")
                .build();
    }

    /**
     * Genera un token JWT ya expirado usando la misma clave del servicio.
     */
    private String buildExpiredToken(String subject) {
        byte[] keyBytes = Decoders.BASE64.decode(TEST_SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "NORMAL");
        claims.put("uid", 1L);
        claims.put("name", "Expired");
        claims.put("lastName", "User");
        claims.put("profileUsername", "expired");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis() - 7_200_000L))
                .setExpiration(new Date(System.currentTimeMillis() - 3_600_000L))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Genera un token JWT firmado con una clave distinta a la configurada en el servicio.
     */
    private String buildTokenWithDifferentKey(String subject) {
        String otherKey = "b3RoZXIta2V5LXRoYXQtaXMtbm90LXRoZS1zYW1lLWFzLXRlc3Qta2V5eA==";
        byte[] keyBytes = Decoders.BASE64.decode(otherKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000L))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
