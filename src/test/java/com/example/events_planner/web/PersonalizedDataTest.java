package com.example.events_planner.web;

import com.example.events_planner.entity.Event;
import com.example.events_planner.entity.Task;
import com.example.events_planner.entity.User;
import com.example.events_planner.repository.EventRepository;
import com.example.events_planner.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class PersonalizedDataTest extends BaseWebTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setup() {
        setupMockMvc();
        taskRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user1")
    void shouldOnlySeeEventsRelatedToUser() throws Exception {
        User user1 = createAndSaveUser("user1", "password", "ROLE_USER");
        User user2 = createAndSaveUser("user2", "password", "ROLE_USER");

        Event event1 = new Event();
        event1.setTitle("Event 1");
        event1.setDate(OffsetDateTime.now().plusDays(1));
        event1.getOrganizers().add(user1);
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setTitle("Event 2");
        event2.setDate(OffsetDateTime.now().plusDays(2));
        event2.getMembers().add(user2);
        eventRepository.save(event2);

        mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Event 1"));
    }

    @Test
    @WithMockUser(username = "user1")
    void shouldOnlySeeTasksAssignedToUser() throws Exception {
        User user1 = createAndSaveUser("user1", "password", "ROLE_USER");
        User user2 = createAndSaveUser("user2", "password", "ROLE_USER");

        Task task1 = new Task();
        task1.setDescription("Task 1");
        task1.setAssignedTo(user1);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setDescription("Task 2");
        task2.setAssignedTo(user2);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Task 1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldSeeAllEventsAndTasks() throws Exception {
        createAndSaveUser("admin", "password", "ROLE_ADMIN");
        User user1 = createAndSaveUser("user1", "password", "ROLE_USER");

        Event event1 = new Event();
        event1.setTitle("Event 1");
        event1.setDate(OffsetDateTime.now().plusDays(1));
        event1.getOrganizers().add(user1);
        eventRepository.save(event1);

        Task task1 = new Task();
        task1.setDescription("Task 1");
        task1.setAssignedTo(user1);
        taskRepository.save(task1);

        mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}