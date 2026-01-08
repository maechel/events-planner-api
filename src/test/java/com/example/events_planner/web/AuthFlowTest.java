package com.example.events_planner.web;

import com.example.events_planner.controller.auth.AuthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthFlowTest extends BaseWebTest {

    @BeforeEach
    void setup() {
        setupMockMvc();

        // Ensure users are not locked and have 0 failed attempts before each test
        userRepository.findByUsername("user").ifPresent(u -> {
            u.setAccountNonLocked(true);
            u.setFailedLoginAttempts(0);
            userRepository.save(u);
        });
        userRepository.findByUsername("testuser").ifPresent(u -> {
            u.setAccountNonLocked(true);
            u.setFailedLoginAttempts(0);
            userRepository.save(u);
        });
    }

    @Test
    void loginWithValidCredentialsShouldSucceed() throws Exception {
        // 'user' with password 'password123' is created in data.sql
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest("user", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Check if session is established by calling /api/me
        jakarta.servlet.http.HttpSession session = result.getRequest().getSession(false);
        assertThat(session).isNotNull();

        mockMvc.perform(get("/api/me")
                .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void loginWithInvalidCredentialsShouldFail() throws Exception {
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest("user", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessingProtectedResourceWithoutLoginShouldFail() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
    }

    @Test
    void logoutShouldInvalidateSession() throws Exception {
        // 1. Login
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest("user", "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        jakarta.servlet.http.HttpSession session = loginResult.getRequest().getSession(false);
        assertThat(session).isNotNull();
        org.springframework.mock.web.MockHttpSession mockSession = (org.springframework.mock.web.MockHttpSession) session;

        // 2. Verify we are logged in
        mockMvc.perform(get("/api/me").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true));

        // 3. Logout
        mockMvc.perform(post("/api/auth/logout").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));

        // 4. Verify we are logged out
        mockMvc.perform(get("/api/me").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
    }

    @Test
    void failedLoginShouldIncrementCounter() throws Exception {
        String username = "user";
        com.example.events_planner.entity.User userBefore = userRepository.findByUsername(username)
                .orElseThrow(() -> new AssertionError("User not found: " + username));
        int initialAttempts = userBefore.getFailedLoginAttempts();

        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest(username, "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        com.example.events_planner.entity.User userAfter = userRepository.findByUsername(username)
                .orElseThrow(() -> new AssertionError("User not found: " + username));
        int attemptsAfterFailure = userAfter.getFailedLoginAttempts();
        assertThat(attemptsAfterFailure).isEqualTo(initialAttempts + 1);
    }

    @Test
    void successfulLoginShouldResetCounter() throws Exception {
        String username = "user";
        // Manual increment
        userRepository.findByUsername(username).ifPresent(u -> {
            u.setFailedLoginAttempts(3);
            userRepository.save(u);
        });

        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest(username, "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        com.example.events_planner.entity.User userAfter = userRepository.findByUsername(username)
                .orElseThrow(() -> new AssertionError("User not found: " + username));
        int attemptsAfterSuccess = userAfter.getFailedLoginAttempts();
        assertThat(attemptsAfterSuccess).isEqualTo(0);
    }

}
