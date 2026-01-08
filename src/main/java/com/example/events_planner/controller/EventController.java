package com.example.events_planner.controller;

import com.example.events_planner.dto.EventDetailDTO;
import com.example.events_planner.dto.EventRequestDTO;
import com.example.events_planner.dto.EventSummaryDTO;
import com.example.events_planner.dto.UserSummaryDTO;
import com.example.events_planner.exception.ResourceNotFoundException;
import com.example.events_planner.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventSummaryDTO> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDetailDTO> getEventById(@PathVariable("id") UUID id) {
        EventDetailDTO event = eventService.getEventById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public EventDetailDTO createEvent(@Valid @RequestBody EventRequestDTO event) {
        return eventService.createEvent(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDetailDTO> updateEvent(@PathVariable("id") UUID id, @Valid @RequestBody EventRequestDTO eventDetails) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<Set<UserSummaryDTO>> getMembers(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(eventService.getMembers(id));
    }

    @GetMapping("/{id}/organizers")
    public ResponseEntity<Set<UserSummaryDTO>> getOrganizers(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(eventService.getOrganizers(id));
    }

    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<EventDetailDTO> addMember(@PathVariable("id") UUID id, @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(eventService.addMember(id, userId));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<EventDetailDTO> removeMember(@PathVariable("id") UUID id, @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(eventService.removeMember(id, userId));
    }

    @PostMapping("/{id}/organizers/{userId}")
    public ResponseEntity<EventDetailDTO> addOrganizer(@PathVariable("id") UUID id, @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(eventService.addOrganizer(id, userId));
    }

    @DeleteMapping("/{id}/organizers/{userId}")
    public ResponseEntity<EventDetailDTO> removeOrganizer(@PathVariable("id") UUID id, @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(eventService.removeOrganizer(id, userId));
    }
}
