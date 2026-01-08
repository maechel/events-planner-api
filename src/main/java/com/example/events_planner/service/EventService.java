package com.example.events_planner.service;

import com.example.events_planner.entity.*;
import com.example.events_planner.dto.*;
import com.example.events_planner.exception.ResourceNotFoundException;
import com.example.events_planner.repository.EventRepository;
import com.example.events_planner.repository.TaskRepository;
import com.example.events_planner.repository.UserRepository;
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

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    public EventService(EventRepository eventRepository, UserRepository userRepository, TaskRepository taskRepository, UserService userService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public List<EventSummaryDTO> getAllEvents() {
        UserAccount currentUser = userService.getCurrentUser();
        List<Event> events;
        if (currentUser.getAuthorities().contains("ROLE_ADMIN")) {
            events = eventRepository.findAll();
        } else {
            events = eventRepository.findByOrganizerOrMember(currentUser.getId());
        }
        
        return events.stream()
                .map(event -> new EventSummaryDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getLocationName() : null,
                        (long) event.getMembers().size() + event.getOrganizers().size(),
                    event.getTasks().size(),
                        event.getTasks().stream().anyMatch(task -> !task.isCompleted())
                ))
                .toList();
    }

    public Optional<EventDetailDTO> getEventById(UUID id) {
        return eventRepository.findById(id).map(this::convertToDetailDTO);
    }

    private EventDetailDTO convertToDetailDTO(Event event) {
        AddressDTO addressDTO = event.getAddress() != null ? new AddressDTO(
                event.getAddress().getId(),
                event.getAddress().getStreet(),
                event.getAddress().getCity(),
                event.getAddress().getZipCode(),
                event.getAddress().getCountry(),
                event.getAddress().getLocationName()
        ) : null;

        Set<UserSummaryDTO> organizers = event.getOrganizers().stream()
                .map(this::convertToUserSummaryDTO)
                .collect(Collectors.toSet());

        Set<UserSummaryDTO> members = event.getMembers().stream()
                .map(this::convertToUserSummaryDTO)
                .collect(Collectors.toSet());

        Set<TaskSummaryDTO> tasks = event.getTasks().stream()
                .map(this::convertToTaskSummaryDTO)
                .collect(Collectors.toSet());

        return new EventDetailDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                addressDTO,
                organizers,
                members,
                tasks,
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }

    private TaskSummaryDTO convertToTaskSummaryDTO(Task task) {
        return new TaskSummaryDTO(
                task.getId(),
                task.getDescription(),
                task.isCompleted(),
                task.getDueDate(),
                task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
                task.getAssignedTo() != null ? task.getAssignedTo().getUsername() : null,
                task.getEvent() != null ? task.getEvent().getId() : null
        );
    }

    public EventDetailDTO createEvent(EventRequestDTO request) {
        Event event = new Event();
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setDate(request.date());
        
        if (request.street() != null || request.city() != null || request.locationName() != null) {
            Address address = new Address();
            address.setStreet(request.street());
            address.setCity(request.city());
            address.setZipCode(request.zipCode());
            address.setCountry(request.country());
            address.setLocationName(request.locationName());
            event.setAddress(address);
        }

        return convertToDetailDTO(eventRepository.save(event));
    }

    public EventDetailDTO updateEvent(UUID id, EventRequestDTO request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));

        if (request.date() != null) {
            List<Task> tasks = taskRepository.findByEventId(id);
            for (Task task : tasks) {
                if (task.getDueDate() != null && request.date().isBefore(task.getDueDate())) {
                    throw new IllegalArgumentException("Event date cannot be before task due date: " + task.getDescription());
                }
            }
            event.setDate(request.date());
        }

        event.setTitle(request.title());
        event.setDescription(request.description());
        
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

        return convertToDetailDTO(eventRepository.save(event));
    }

    public Set<UserSummaryDTO> getMembers(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        return event.getMembers().stream()
                .map(this::convertToUserSummaryDTO)
                .collect(Collectors.toSet());
    }

    public Set<UserSummaryDTO> getOrganizers(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        return event.getOrganizers().stream()
                .map(this::convertToUserSummaryDTO)
                .collect(Collectors.toSet());
    }

    private UserSummaryDTO convertToUserSummaryDTO(UserAccount user) {
        return new UserSummaryDTO(user.getId(), user.getUsername(), user.getAvatar());
    }

    public void deleteEvent(UUID id) {
        eventRepository.deleteById(id);
    }

    public EventDetailDTO addMember(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        event.getMembers().add(user);
        return convertToDetailDTO(eventRepository.save(event));
    }

    public EventDetailDTO removeMember(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        event.getMembers().remove(user);
        return convertToDetailDTO(eventRepository.save(event));
    }

    public EventDetailDTO addOrganizer(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        event.getOrganizers().add(user);
        return convertToDetailDTO(eventRepository.save(event));
    }

    public EventDetailDTO removeOrganizer(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        event.getOrganizers().remove(user);
        return convertToDetailDTO(eventRepository.save(event));
    }
}
