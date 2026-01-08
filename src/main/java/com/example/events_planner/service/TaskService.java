package com.example.events_planner.service;

import com.example.events_planner.dto.TaskDetailDTO;
import com.example.events_planner.dto.TaskRequestDTO;
import com.example.events_planner.dto.TaskSummaryDTO;
import com.example.events_planner.entity.Event;
import com.example.events_planner.entity.Task;
import com.example.events_planner.entity.User;
import com.example.events_planner.exception.ResourceNotFoundException;
import com.example.events_planner.mapper.TaskMapper;
import com.example.events_planner.repository.EventRepository;
import com.example.events_planner.repository.TaskRepository;
import com.example.events_planner.repository.UserRepository;
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
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, EventRepository eventRepository, UserRepository userRepository, UserService userService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.taskMapper = taskMapper;
    }

    public List<TaskSummaryDTO> getAllTasks() {
        User currentUser = userService.getCurrentUser();
        List<Task> tasks;
        if (currentUser.getAuthorities().contains("ROLE_ADMIN")) {
            tasks = taskRepository.findAll();
        } else {
            tasks = taskRepository.findByAssignedToId(currentUser.getId());
        }
        
        return tasks.stream()
                .map(taskMapper::toSummaryDTO)
                .toList();
    }

    public List<TaskSummaryDTO> getTasksByEventId(UUID eventId) {
        return taskRepository.findByEventId(eventId).stream()
                .map(taskMapper::toSummaryDTO)
                .toList();
    }

    public Optional<TaskDetailDTO> getTaskById(UUID id) {
        return taskRepository.findById(id).map(taskMapper::toDetailDTO);
    }

    public TaskDetailDTO createTask(TaskRequestDTO request) {
        Task task = new Task();
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setCompleted(request.completed() != null && request.completed());

        if (request.eventId() != null) {
            Event event = eventRepository.findById(request.eventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + request.eventId()));
            
            validateTaskDueDate(task, event);
            task.setEvent(event);
        }

        if (request.assignedToId() != null) {
            User user = userRepository.findById(request.assignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + request.assignedToId()));
            task.setAssignedTo(user);
        }

        return taskMapper.toDetailDTO(taskRepository.save(task));
    }

    private void validateTaskDueDate(Task task, Event event) {
        if (task.getDueDate() != null && task.getDueDate().isAfter(event.getDate())) {
            throw new IllegalArgumentException("Task due date cannot be after event date");
        }
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
            
            validateTaskDueDate(task, event);
            task.setEvent(event);
        } else if (task.getEvent() != null) {
            validateTaskDueDate(task, task.getEvent());
        }

        if (request.assignedToId() != null) {
            User user = userRepository.findById(request.assignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + request.assignedToId()));
            task.setAssignedTo(user);
        } else {
            task.setAssignedTo(null);
        }

        return taskMapper.toDetailDTO(taskRepository.save(task));
    }

    public TaskDetailDTO toggleTaskCompletion(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
        task.setCompleted(!task.isCompleted());
        return taskMapper.toDetailDTO(taskRepository.save(task));
    }

    public void deleteTask(UUID id) {
        taskRepository.deleteById(id);
    }
}
