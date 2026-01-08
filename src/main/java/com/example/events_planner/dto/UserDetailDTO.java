package com.example.events_planner.dto;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record UserDetailDTO(
    UUID id,
    String username,
    String email,
    boolean enabled,
    boolean accountNonLocked,
    String avatar,
    int failedLoginAttempts,
    OffsetDateTime lastLogin,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    Set<String> authorities
) {}
