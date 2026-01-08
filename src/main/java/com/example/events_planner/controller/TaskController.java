package com.example.events_planner.controller;

import com.example.events_planner.dto.*;
import com.example.events_planner.exception.ResourceNotFoundException;
import com.example.events_planner.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskSummaryDTO> getAllTasks(@RequestParam(value = "eventId", required = false) UUID eventId) {
        if (eventId != null) {
            return taskService.getTasksByEventId(eventId);
        }
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDetailDTO> getTaskById(@PathVariable("id") UUID id) {
        TaskDetailDTO task = taskService.getTaskById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public TaskDetailDTO createTask(@RequestBody TaskRequestDTO task) {
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDetailDTO> updateTask(@PathVariable("id") UUID id, @RequestBody TaskRequestDTO taskDetails) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{id}/toggle", method = {RequestMethod.PATCH, RequestMethod.POST})
    public ResponseEntity<TaskDetailDTO> toggleTaskCompletion(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(taskService.toggleTaskCompletion(id));
    }
}
