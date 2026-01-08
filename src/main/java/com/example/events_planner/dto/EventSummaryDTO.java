package com.example.events_planner.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EventSummaryDTO(
    UUID id,
    String title,
    String description,
    OffsetDateTime date,
    String locationName,
    long participantCount,
    long taskCount,
    boolean hasUnfinishedTasks
) {}
