package com.example.events_planner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record EventRequestDTO(
    @NotBlank(message = "Title is required")
    String title,
    
    String description,
    
    @NotNull(message = "Event date is required")
    OffsetDateTime date,
    
    String locationName,
    String street,
    String city,
    String zipCode,
    String country
) {}
