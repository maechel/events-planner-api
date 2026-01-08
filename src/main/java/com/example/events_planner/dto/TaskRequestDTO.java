package com.example.events_planner.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskRequestDTO(
    String description,
    UUID assignedToId,
    OffsetDateTime dueDate,
    UUID eventId,
    Boolean completed
) {}
