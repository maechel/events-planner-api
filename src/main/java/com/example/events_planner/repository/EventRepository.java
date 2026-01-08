package com.example.events_planner.repository;

import com.example.events_planner.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query("SELECT COUNT(DISTINCT o.id) FROM Event e JOIN e.organizers o")
    long countDistinctOrganizers();

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN e.organizers o LEFT JOIN e.members m WHERE o.id = :userId OR m.id = :userId")
    List<Event> findByOrganizerOrMember(@Param("userId") UUID userId);
}
