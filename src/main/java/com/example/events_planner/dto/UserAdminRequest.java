package com.example.events_planner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record UserAdminRequest(
    @NotBlank(message = "Username is required")
    String username,
    
    @Email(message = "Invalid email format")
    String email,
    
    String password,
    Boolean enabled,
    Boolean accountNonLocked,
    String avatar,
    Set<String> authorities
) {}
