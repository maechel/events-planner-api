package com.example.events_planner.mapper;

import com.example.events_planner.dto.AddressDTO;
import com.example.events_planner.dto.EventDetailDTO;
import com.example.events_planner.dto.EventSummaryDTO;
import com.example.events_planner.entity.Event;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EventMapper {

    private final UserMapper userMapper;
    private final TaskMapper taskMapper;

    public EventMapper(UserMapper userMapper, TaskMapper taskMapper) {
        this.userMapper = userMapper;
        this.taskMapper = taskMapper;
    }

    public EventSummaryDTO toSummaryDTO(Event event) {
        if (event == null) return null;
        return new EventSummaryDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : null,
                event.getMembers().size() + event.getOrganizers().size(),
                event.getTasks().size(),
                event.getTasks().stream().anyMatch(task -> !task.isCompleted())
        );
    }

    public EventDetailDTO toDetailDTO(Event event) {
        if (event == null) return null;
        return new EventDetailDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? new AddressDTO(
                        event.getAddress().getId(),
                        event.getAddress().getStreet(),
                        event.getAddress().getCity(),
                        event.getAddress().getZipCode(),
                        event.getAddress().getCountry(),
                        event.getAddress().getLocationName(),
                        event.getAddress().getGeo() != null ? event.getAddress().getGeo().latitude() : null,
                        event.getAddress().getGeo() != null ? event.getAddress().getGeo().longitude() : null
                ) : null,
                event.getOrganizers().stream().map(userMapper::toSummaryDTO).collect(Collectors.toSet()),
                event.getMembers().stream().map(userMapper::toSummaryDTO).collect(Collectors.toSet()),
                event.getTasks().stream().map(taskMapper::toSummaryDTO).collect(Collectors.toSet()),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
