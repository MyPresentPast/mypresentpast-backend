package com.mypresentpast.backend.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.mypresentpast.backend.dto.response.LikeStatusResponse;
import com.mypresentpast.backend.dto.response.LikeToggleResponse;
import com.mypresentpast.backend.service.JwtService;
import com.mypresentpast.backend.service.LikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LikeControllerIntegrationTest {

    @Autowired
    private LikeService likeService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void likeServiceIsLoaded() {
        // Test que verifica que el servicio se carga correctamente
        assertNotNull(likeService);
    }

    @Test
    void canCreateLikeToggleResponse() {
        // Given - Test que verifica que se puede crear response
        LikeToggleResponse response = new LikeToggleResponse();
        response.setMessage("Test message");
        response.setIsLiked(true);
        response.setTotalLikes(5L);

        // Then
        assertNotNull(response);
        assertNotNull(response.getMessage());
        assertNotNull(response.getIsLiked());
        assertNotNull(response.getTotalLikes());
    }

    @Test
    void canCreateLikeStatusResponse() {
        // Given - Test que verifica que se puede crear response
        LikeStatusResponse response = new LikeStatusResponse();
        response.setIsLiked(false);
        response.setTotalLikes(10L);

        // Then
        assertNotNull(response);
        assertNotNull(response.getIsLiked());
        assertNotNull(response.getTotalLikes());
    }
}
