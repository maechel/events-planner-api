package com.example.events_planner.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record Geo(double latitude, double longitude) {
    public Geo() {
        this(0, 0);
    }
}
