package com.example.events_planner.mapper;

import com.example.events_planner.dto.TaskDetailDTO;
import com.example.events_planner.dto.TaskSummaryDTO;
import com.example.events_planner.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    private final UserMapper userMapper;

    public TaskMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public TaskSummaryDTO toSummaryDTO(Task task) {
        if (task == null) return null;
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

    public TaskDetailDTO toDetailDTO(Task task) {
        if (task == null) return null;
        return new TaskDetailDTO(
                task.getId(),
                task.getDescription(),
                task.isCompleted(),
                task.getDueDate(),
                userMapper.toSummaryDTO(task.getAssignedTo()),
                task.getEvent() != null ? task.getEvent().getId() : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
