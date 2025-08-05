package com.mypresentpast.backend.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.mypresentpast.backend.repository.LocationRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BasicIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void repositoriesAreLoaded() {
        // Test que verifica que los repositorios se cargan correctamente
        assertNotNull(postRepository);
        assertNotNull(userRepository);
        assertNotNull(locationRepository);
    }

    @Test
    void postRepository_canFindAll() {
        // When
        var posts = postRepository.findAll();

        // Then
        assertNotNull(posts);
    }

    @Test
    void userRepository_canFindAll() {
        // When
        var users = userRepository.findAll();

        // Then
        assertNotNull(users);
    }

    @Test
    void locationRepository_canFindAll() {
        // When
        var locations = locationRepository.findAll();

        // Then
        assertNotNull(locations);
    }
}