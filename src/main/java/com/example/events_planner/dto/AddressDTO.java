package com.example.events_planner.dto;

import java.util.UUID;

public record AddressDTO(
    UUID id,
    String street,
    String city,
    String zipCode,
    String country,
    String locationName,
    Double latitude,
    Double longitude
) {}
