package com.example.events_planner.dto;

import java.util.Set;

public record UserAdminRequest(
    String username,
    String email,
    String password,
    Boolean enabled,
    Boolean accountNonLocked,
    String avatar,
    Set<String> authorities
) {}
