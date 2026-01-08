package com.example.events_planner.repository;

import com.example.events_planner.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByEventId(UUID eventId);
    List<Task> findByAssignedToId(UUID userId);
    long countByCompletedTrue();
}
