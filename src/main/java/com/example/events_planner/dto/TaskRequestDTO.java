package com.example.events_planner.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskRequestDTO(
    @NotBlank(message = "Description is required")
    String description,
    
    UUID assignedToId,
    OffsetDateTime dueDate,
    UUID eventId,
    Boolean completed
) {}
