package com.example.events_planner.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskSummaryDTO(
    UUID id,
    String description,
    boolean completed,
    OffsetDateTime dueDate,
    UUID assignedToId,
    String assignedToUsername,
    UUID eventId
) {}
