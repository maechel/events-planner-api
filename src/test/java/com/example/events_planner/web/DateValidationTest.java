package com.example.events_planner.web;

import com.example.events_planner.entity.Event;
import com.example.events_planner.entity.Task;
import com.example.events_planner.repository.EventRepository;
import com.example.events_planner.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class DateValidationTest extends BaseWebTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setup() {
        setupMockMvc();
        taskRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void shouldFailWhenTaskDueDateIsAfterEventDate() throws Exception {
        Event event = new Event();
        event.setTitle("Future Event");
        event.setDate(OffsetDateTime.now().plusDays(1));
        event = eventRepository.save(event);

        String json = String.format("""
                {
                    "description": "Invalid Task",
                    "dueDate": "%s",
                    "eventId": "%s"
                }
                """, OffsetDateTime.now().plusDays(2), event.getId());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldFailWhenEventDateIsBeforeTaskDueDate() throws Exception {
        Event event = new Event();
        event.setTitle("Valid Event");
        event.setDate(OffsetDateTime.now().plusDays(5));
        event = eventRepository.save(event);

        Task task = new Task();
        task.setDescription("Valid Task");
        task.setDueDate(OffsetDateTime.now().plusDays(4));
        task.setEvent(event);
        taskRepository.save(task);

        String json = String.format("""
                {
                    "title": "Updated Event",
                    "date": "%s"
                }
                """, OffsetDateTime.now().plusDays(3));

        mockMvc.perform(put("/api/events/" + event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldFailWhenUpdatingTaskDueDateIsAfterEventDate() throws Exception {
        Event event = new Event();
        event.setTitle("Event");
        event.setDate(OffsetDateTime.now().plusDays(5));
        event = eventRepository.save(event);

        Task task = new Task();
        task.setDescription("Task");
        task.setDueDate(OffsetDateTime.now().plusDays(4));
        task.setEvent(event);
        task = taskRepository.save(task);

        String json = String.format("""
                {
                    "description": "Updated Task",
                    "dueDate": "%s"
                }
                """, OffsetDateTime.now().plusDays(6));

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldSucceedWhenDatesAreValid() throws Exception {
        Event event = new Event();
        event.setTitle("Valid Event");
        event.setDate(OffsetDateTime.now().plusDays(10));
        event = eventRepository.save(event);

        String json = String.format("""
                {
                    "description": "Valid Task",
                    "dueDate": "%s",
                    "eventId": "%s"
                }
                """, OffsetDateTime.now().plusDays(9), event.getId());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
