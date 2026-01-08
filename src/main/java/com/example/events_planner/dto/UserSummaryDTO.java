package com.example.events_planner.dto;

import java.util.UUID;

public record UserSummaryDTO(
    UUID id,
    String username,
    String avatar
) {}
