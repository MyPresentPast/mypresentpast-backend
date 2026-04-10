package com.mypresentpast.backend.controller;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InstitutionRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void getMyRequests_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/institution-requests/my"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createRequest_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(multipart("/institution-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelRequest_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/institution-requests/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void canCreateNewRequest_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/institution-requests/can-create"))
                .andExpect(status().isForbidden());
    }
}
