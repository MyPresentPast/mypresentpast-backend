package com.mypresentpast.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mypresentpast.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AIControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void correctContent_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/ai/correct-content")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\": \"texto a corregir\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void generatePost_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/ai/generate-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\": \"2020-01-01\", \"location\": \"Buenos Aires\"}"))
                .andExpect(status().isForbidden());
    }
}
