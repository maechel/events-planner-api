package com.example.events_planner.web;

import com.example.events_planner.controller.auth.AuthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RegistrationFlowTest extends BaseWebTest {

    @BeforeEach
    void setUp() {
        setupMockMvc();
        userRepository.findByUsername("newuser").ifPresent(user -> {
            userRepository.delete(user);
            userRepository.flush();
        });
    }

    @Test
    void shouldRegisterNewUser() throws Exception {
        String username = "newuser_" + java.util.UUID.randomUUID();
        AuthController.RegisterRequest registerRequest = new AuthController.RegisterRequest(
                username,
                username + "@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() throws Exception {
        // 'user' is already in data.sql
        AuthController.RegisterRequest registerRequest = new AuthController.RegisterRequest(
                "user",
                "other@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Username already exists"));
    }

    @Test
    void shouldNotRegisterUserWithInvalidData() throws Exception {
        // We bypass DTO creation for invalid data to test server-side validation if needed,
        // or just use invalid values in DTO.
        AuthController.RegisterRequest registerRequest = new AuthController.RegisterRequest(
                "u",
                "not-an-email",
                "123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRegisterAndThenLogin() throws Exception {
        String username = "loginuser_" + java.util.UUID.randomUUID();
        AuthController.RegisterRequest registerRequest = new AuthController.RegisterRequest(
                username,
                username + "@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest(
                username,
                "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }
}
