package com.example.events_planner.mapper;

import com.example.events_planner.dto.UserDetailDTO;
import com.example.events_planner.dto.UserSummaryDTO;
import com.example.events_planner.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserSummaryDTO toSummaryDTO(User user) {
        if (user == null) return null;
        return new UserSummaryDTO(user.getId(), user.getUsername(), user.getAvatar());
    }

    public UserDetailDTO toDetailDTO(User user) {
        if (user == null) return null;
        return new UserDetailDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.isAccountNonLocked(),
                user.getAvatar(),
                user.getFailedLoginAttempts(),
                user.getLastLogin(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getAuthorities()
        );
    }
}
