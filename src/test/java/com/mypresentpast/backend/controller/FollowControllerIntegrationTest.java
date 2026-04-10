package com.mypresentpast.backend.controller;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class FollowControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    // --- Endpoints protegidos ---

    @Test
    void follow_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/follow/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unfollow_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/follow/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyFollowing_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/follow/my-following"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyFollowers_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/follow/my-followers"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getFollowingByUserId_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/follow/1/following"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getFollowersByUserId_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/follow/1/followers"))
                .andExpect(status().isForbidden());
    }

    @Test
    void isFollowing_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/follow/is-following/1"))
                .andExpect(status().isForbidden());
    }

    // --- Endpoint público ---

    @Test
    void getFollowStats_ShouldBeAccessible_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/follow/stats/1"))
                .andExpect(status().is(not(403)));
    }
}
