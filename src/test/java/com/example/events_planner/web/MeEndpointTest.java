package com.example.events_planner.web;

import com.example.events_planner.controller.auth.AuthController;
import com.example.events_planner.entity.User;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MeEndpointTest extends BaseWebTest {

    @BeforeEach
    void setup() {
        setupMockMvc();
    }

    @Test
    void meEndpointShouldIncludeUserId() throws Exception {
        String username = "user";
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AssertionError("User not found: " + username));
        UUID userId = user.getId();

        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest(username, "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        HttpSession session = result.getRequest().getSession(false);
        assertThat(session).isNotNull();

        mockMvc.perform(get("/api/me")
                        .session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }
}
