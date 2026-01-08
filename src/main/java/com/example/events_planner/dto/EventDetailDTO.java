package com.example.events_planner.dto;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record EventDetailDTO(
    UUID id,
    String title,
    String description,
    OffsetDateTime date,
    AddressDTO address,
    Set<UserSummaryDTO> organizers,
    Set<UserSummaryDTO> members,
    Set<TaskSummaryDTO> tasks,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
