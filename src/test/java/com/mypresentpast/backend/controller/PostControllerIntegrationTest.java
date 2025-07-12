//package com.mypresentpast.backend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mypresentpast.backend.repository.PostRepository;
//import com.mypresentpast.backend.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@Transactional
//class PostControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Test
//    void repositories_AreInjected() {
//        // Simple test para verificar que las dependencias están inyectadas
//        assert userRepository != null;
//        assert postRepository != null;
//        assert mockMvc != null;
//    }
//}