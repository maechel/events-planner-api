package com.example.events_planner.dto;

public record AdminStatsDTO(
    long totalUsers,
    long totalEvents,
    long totalTasks,
    long completedTasks,
    double taskCompletionRate,
    long activeOrganizers
) {}
