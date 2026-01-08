package com.example.events_planner.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskDetailDTO(
    UUID id,
    String description,
    boolean completed,
    OffsetDateTime dueDate,
    UserSummaryDTO assignedTo,
    UUID eventId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
