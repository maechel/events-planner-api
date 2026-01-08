package com.example.events_planner.controller;

import com.example.events_planner.dto.AdminStatsDTO;
import com.example.events_planner.dto.UserAdminRequest;
import com.example.events_planner.dto.UserDetailDTO;
import com.example.events_planner.exception.ResourceNotFoundException;
import com.example.events_planner.service.StatisticsService;
import com.example.events_planner.service.UserService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final StatisticsService statisticsService;
    private final UserService userService;

    public AdminController(StatisticsService statisticsService, UserService userService) {
        this.statisticsService = statisticsService;
        this.userService = userService;
    }

    @GetMapping("/stats")
    public AdminStatsDTO getStats() {
        return statisticsService.getAdminStats();
    }

    @GetMapping("/users")
    public List<UserDetailDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDetailDTO> getUserById(@PathVariable("id") UUID id) {
        UserDetailDTO user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public UserDetailDTO createUser(@Valid @RequestBody UserAdminRequest user) {
        return userService.createUser(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDetailDTO> updateUser(@PathVariable("id") UUID id, @Valid @RequestBody UserAdminRequest userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
