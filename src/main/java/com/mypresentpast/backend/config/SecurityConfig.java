package com.mypresentpast.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Configura la seguridad de la aplicación.
 * - Desactiva CSRF (ya que usamos JWT, no sesiones).
 * - Define qué endpoints son públicos y cuáles requieren autenticación.
 * - Establece el filtro JWT para validar tokens en cada petición.
 * - Configura la política de sesiones como "stateless" (sin estado).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authRequest -> authRequest
                        .requestMatchers("/auth/**").permitAll() // Solo los endpoints de auth van a ser publicos.
                        .requestMatchers(AUTH_WHITELIST).permitAll() // Son endpoints publicos definidos en la constante AUTH_WHITELIST.
                        .requestMatchers("/posts/*/verify").hasRole("INSTITUTION") // Solo instituciones pueden verificar posts
                        .anyRequest().authenticated() // El resto de request van a tener que estar autenticadas si o si.
                )
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers().frameOptions().disable()
                .and()
                .build();

    }

    // Constante donde podemos poner urls sin autenticacion
    private static final String[] AUTH_WHITELIST = {
        // Health check
        "/ping",
        
        // Perfiles públicos (para ver sin login)
        "/profiles/**",
        
        // Posts públicos (consulta sin login)
        "/posts/map",
        "/posts/random", 
        "/posts/{id}",
        "/posts/user/{id}",
        
        // Categorías públicas
        "/categories",
        
        // Otros endpoints públicos de consulta
        "/follow/stats/**"
    };

}
