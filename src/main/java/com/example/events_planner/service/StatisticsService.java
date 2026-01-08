package com.example.events_planner.service;

import com.example.events_planner.repository.EventRepository;
import com.example.events_planner.repository.TaskRepository;
import com.example.events_planner.repository.UserRepository;
import com.example.events_planner.dto.AdminStatsDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TaskRepository taskRepository;

    public StatisticsService(UserRepository userRepository, EventRepository eventRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.taskRepository = taskRepository;
    }

    public AdminStatsDTO getAdminStats() {
        long totalUsers = userRepository.count();
        long totalEvents = eventRepository.count();
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByCompletedTrue();
        
        double taskCompletionRate = totalTasks > 0 
            ? (double) completedTasks / totalTasks 
            : 0.0;
            
        long activeOrganizers = eventRepository.countDistinctOrganizers();

        return new AdminStatsDTO(
            totalUsers,
            totalEvents,
            totalTasks,
            completedTasks,
            taskCompletionRate,
            activeOrganizers
        );
    }
}
