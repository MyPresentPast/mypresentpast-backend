package com.mypresentpast.backend.controller;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    // --- Endpoint público ---

    @Test
    void getProfile_ShouldBeAccessible_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/profiles/1"))
                .andExpect(status().is(not(403)));
    }

    // --- Endpoints protegidos ---

    @Test
    void updateProfile_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(patch("/profiles/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void changePassword_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(put("/profiles/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadAvatar_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PUT, "/profiles/me/avatar"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyLikedPosts_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/profiles/me/liked-posts"))
                .andExpect(status().isForbidden());
    }
}
