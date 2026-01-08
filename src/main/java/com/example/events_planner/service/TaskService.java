package com.example.events_planner.service;

import com.example.events_planner.entity.*;
import com.example.events_planner.repository.*;
import com.example.events_planner.dto.*;
import com.example.events_planner.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, EventRepository eventRepository, UserRepository userRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<TaskSummaryDTO> getAllTasks() {
        UserAccount currentUser = userService.getCurrentUser();
        List<Task> tasks;
        if (currentUser.getAuthorities().contains("ROLE_ADMIN")) {
            tasks = taskRepository.findAll();
        } else {
            tasks = taskRepository.findByAssignedToId(currentUser.getId());
        }
        
        return tasks.stream()
                .map(this::convertToSummaryDTO)
                .toList();
    }

    public List<TaskSummaryDTO> getTasksByEventId(UUID eventId) {
        return taskRepository.findByEventId(eventId).stream()
                .map(this::convertToSummaryDTO)
                .toList();
    }

    private TaskSummaryDTO convertToSummaryDTO(Task task) {
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

    public Optional<TaskDetailDTO> getTaskById(UUID id) {
        return taskRepository.findById(id).map(this::convertToDetailDTO);
    }

    private TaskDetailDTO convertToDetailDTO(Task task) {
        UserSummaryDTO assignedTo = task.getAssignedTo() != null ? new UserSummaryDTO(
                task.getAssignedTo().getId(),
                task.getAssignedTo().getUsername(),
                task.getAssignedTo().getAvatar()
        ) : null;

        return new TaskDetailDTO(
                task.getId(),
                task.getDescription(),
                task.isCompleted(),
                task.getDueDate(),
                assignedTo,
                task.getEvent() != null ? task.getEvent().getId() : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public TaskDetailDTO createTask(TaskRequestDTO request) {
        Task task = new Task();
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setCompleted(request.completed() != null && request.completed());

        if (request.eventId() != null) {
            Event event = eventRepository.findById(request.eventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + request.eventId()));
            
            if (task.getDueDate() != null && task.getDueDate().isAfter(event.getDate())) {
                throw new IllegalArgumentException("Task due date cannot be after event date");
            }
            task.setEvent(event);
        }

        if (request.assignedToId() != null) {
            UserAccount user = userRepository.findById(request.assignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + request.assignedToId()));
            task.setAssignedTo(user);
        }

        return convertToDetailDTO(taskRepository.save(task));
    }

    public TaskDetailDTO updateTask(UUID id, TaskRequestDTO request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));

        task.setDescription(request.description());
        if (request.completed() != null) {
            task.setCompleted(request.completed());
        }
        task.setDueDate(request.dueDate());

        if (request.eventId() != null) {
            Event event = eventRepository.findById(request.eventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + request.eventId()));
            
            if (task.getDueDate() != null && task.getDueDate().isAfter(event.getDate())) {
                throw new IllegalArgumentException("Task due date cannot be after event date");
            }
            task.setEvent(event);
        } else if (task.getEvent() != null && task.getDueDate() != null) {
            if (task.getDueDate().isAfter(task.getEvent().getDate())) {
                throw new IllegalArgumentException("Task due date cannot be after event date");
            }
        }

        if (request.assignedToId() != null) {
            UserAccount user = userRepository.findById(request.assignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + request.assignedToId()));
            task.setAssignedTo(user);
        } else {
            task.setAssignedTo(null);
        }

        return convertToDetailDTO(taskRepository.save(task));
    }

    public TaskDetailDTO toggleTaskCompletion(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
        task.setCompleted(!task.isCompleted());
        return convertToDetailDTO(taskRepository.save(task));
    }

    public void deleteTask(UUID id) {
        taskRepository.deleteById(id);
    }
}
