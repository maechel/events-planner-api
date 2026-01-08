package com.example.events_planner.dto;

import java.time.OffsetDateTime;

public record EventRequestDTO(
    String title,
    String description,
    OffsetDateTime date,
    String locationName,
    String street,
    String city,
    String zipCode,
    String country
) {}
