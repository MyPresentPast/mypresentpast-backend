package com.mypresentpast.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypresentpast.backend.dto.request.RejectRequestDto;
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
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void getAllRequests_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/admin/institution-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRequestDetail_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/admin/institution-requests/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void approveRequest_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(put("/admin/institution-requests/1/approve"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectRequest_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        RejectRequestDto dto = new RejectRequestDto();
        dto.setRejectionReason("Motivo de rechazo");
        mockMvc.perform(put("/admin/institution-requests/1/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
