package com.mypresentpast.backend.controller;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mypresentpast.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    // --- Endpoints protegidos: deben retornar 403 sin autenticación ---

    @Test
    void createPost_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(multipart("/posts")
                .param("data", "{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePost_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PUT, "/posts/1")
                .param("data", "{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePost_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void verifyPost_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(multipart("/posts/1/verify"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unverifyPost_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/posts/1/verify"))
                .andExpect(status().isForbidden());
    }

    // --- Endpoints públicos: deben ser accesibles sin autenticación ---

    @Test
    void getPostById_ShouldBeAccessible_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/posts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(not(403)));
    }

    @Test
    void getPostsByUser_ShouldBeAccessible_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/posts/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(not(403)));
    }

    @Test
    void getMapData_ShouldBeAccessible_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/posts/map")
                .param("latMin", "-90")
                .param("latMax", "90")
                .param("lonMin", "-180")
                .param("lonMax", "180"))
                .andExpect(status().is(not(403)));
    }

    @Test
    void getRandomPost_ShouldBeAccessible_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/posts/random")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(not(403)));
    }
}