package com.example.events_planner.service;

import com.example.events_planner.dto.EventDetailDTO;
import com.example.events_planner.dto.EventRequestDTO;
import com.example.events_planner.dto.EventSummaryDTO;
import com.example.events_planner.dto.UserSummaryDTO;
import com.example.events_planner.entity.Address;
import com.example.events_planner.entity.Event;
import com.example.events_planner.entity.Task;
import com.example.events_planner.entity.User;
import com.example.events_planner.exception.ResourceNotFoundException;
import com.example.events_planner.mapper.EventMapper;
import com.example.events_planner.mapper.UserMapper;
import com.example.events_planner.repository.EventRepository;
import com.example.events_planner.repository.TaskRepository;
import com.example.events_planner.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    public EventService(EventRepository eventRepository, UserRepository userRepository, 
                        TaskRepository taskRepository, UserService userService, 
                        EventMapper eventMapper, UserMapper userMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
    }

    public List<EventSummaryDTO> getAllEvents() {
        User currentUser = userService.getCurrentUser();
        log.debug("Fetching all events for user: {}", currentUser.getUsername());
        List<Event> events;
        if (currentUser.getAuthorities().contains("ROLE_ADMIN")) {
            events = eventRepository.findAll();
        } else {
            events = eventRepository.findByOrganizerOrMember(currentUser.getId());
        }
        
        return events.stream()
                .map(eventMapper::toSummaryDTO)
                .toList();
    }

    public Optional<EventDetailDTO> getEventById(UUID id) {
        return eventRepository.findById(id).map(eventMapper::toDetailDTO);
    }

    public EventDetailDTO createEvent(EventRequestDTO request) {
        log.info("Creating new event with title: {}", request.title());
        Event event = new Event();
        updateEventFields(event, request);
        return eventMapper.toDetailDTO(eventRepository.save(event));
    }

    public EventDetailDTO updateEvent(UUID id, EventRequestDTO request) {
        log.info("Updating event with id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Event update failed: Event not found with id {}", id);
                    return new ResourceNotFoundException("Event not found with id " + id);
                });

        if (request.date() != null) {
            validateEventDate(id, request.date());
        }

        updateEventFields(event, request);
        return eventMapper.toDetailDTO(eventRepository.save(event));
    }

    private void validateEventDate(UUID eventId, java.time.OffsetDateTime newDate) {
        List<Task> tasks = taskRepository.findByEventId(eventId);
        for (Task task : tasks) {
            if (task.getDueDate() != null && newDate.isBefore(task.getDueDate())) {
                throw new IllegalArgumentException("Event date cannot be before task due date: " + task.getDescription());
            }
        }
    }

    private void updateEventFields(Event event, EventRequestDTO request) {
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setDate(request.date());

        if (request.street() != null || request.city() != null || request.locationName() != null) {
            Address address = event.getAddress();
            if (address == null) {
                address = new Address();
            }
            address.setStreet(request.street());
            address.setCity(request.city());
            address.setZipCode(request.zipCode());
            address.setCountry(request.country());
            address.setLocationName(request.locationName());
            event.setAddress(address);
        }
    }

    public Set<UserSummaryDTO> getMembers(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        return event.getMembers().stream()
                .map(userMapper::toSummaryDTO)
                .collect(Collectors.toSet());
    }

    public Set<UserSummaryDTO> getOrganizers(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        return event.getOrganizers().stream()
                .map(userMapper::toSummaryDTO)
                .collect(Collectors.toSet());
    }

    public void deleteEvent(UUID id) {
        eventRepository.deleteById(id);
    }

    public EventDetailDTO addMember(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        event.getMembers().add(user);
        return eventMapper.toDetailDTO(eventRepository.save(event));
    }

    public EventDetailDTO removeMember(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        event.getMembers().remove(user);
        return eventMapper.toDetailDTO(eventRepository.save(event));
    }

    public EventDetailDTO addOrganizer(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        event.getOrganizers().add(user);
        return eventMapper.toDetailDTO(eventRepository.save(event));
    }

    public EventDetailDTO removeOrganizer(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        event.getOrganizers().remove(user);
        return eventMapper.toDetailDTO(eventRepository.save(event));
    }
}
