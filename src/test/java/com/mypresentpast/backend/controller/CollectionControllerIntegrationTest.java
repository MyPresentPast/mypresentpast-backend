package com.mypresentpast.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypresentpast.backend.dto.request.CreateCollectionAndSavePostRequest;
import com.mypresentpast.backend.dto.request.CreateCollectionRequest;
import com.mypresentpast.backend.dto.request.UpdateCollectionRequest;
import com.mypresentpast.backend.repository.CollectionPostRepository;
import com.mypresentpast.backend.repository.CollectionRepository;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class CollectionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionPostRepository collectionPostRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void repositories_AreInjected() {
        // Test básico para verificar que las dependencias están inyectadas
        assert collectionRepository != null;
        assert collectionPostRepository != null;
        assert postRepository != null;
        assert userRepository != null;
        assert mockMvc != null;
    }

    @Test
    void getMyCollections_ShouldReturnForbidden_WhenNoJWT() throws Exception {
        // When & Then
        mockMvc.perform(get("/collections/my")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // Tests de validación básica simplificados - solo verificamos que requieren autenticación
    // Los tests de lógica de negocio están en CollectionServiceImplTest

    @Test
    void allEndpoints_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        // Test que verifica que todos los endpoints requieren autenticación

        // GET /collections/my
        mockMvc.perform(get("/collections/my"))
                .andExpect(status().isForbidden());

        // POST /collections
        CreateCollectionRequest createRequest = new CreateCollectionRequest();
        createRequest.setName("Test");
        mockMvc.perform(post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        // PUT /collections/{id}
        UpdateCollectionRequest updateRequest = new UpdateCollectionRequest();
        updateRequest.setName("Updated");
        mockMvc.perform(put("/collections/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        // DELETE /collections/{id}
        mockMvc.perform(delete("/collections/1"))
                .andExpect(status().isForbidden());

        // GET /collections/{id}/posts
        mockMvc.perform(get("/collections/1/posts"))
                .andExpect(status().isForbidden());

        // GET /collections/my/posts/{postId}/status
        mockMvc.perform(get("/collections/my/posts/1/status"))
                .andExpect(status().isForbidden());

        // POST /collections/{collectionId}/posts/{postId}
        mockMvc.perform(post("/collections/1/posts/1"))
                .andExpect(status().isForbidden());

        // DELETE /collections/{collectionId}/posts/{postId}
        mockMvc.perform(delete("/collections/1/posts/1"))
                .andExpect(status().isForbidden());

        // POST /collections/create-and-save
        CreateCollectionAndSavePostRequest createAndSaveRequest = new CreateCollectionAndSavePostRequest();
        createAndSaveRequest.setName("Test");
        createAndSaveRequest.setPostId(1L);
        mockMvc.perform(post("/collections/create-and-save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAndSaveRequest)))
                .andExpect(status().isForbidden());
    }
}
