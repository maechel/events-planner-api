package com.example.events_planner.service;

import com.example.events_planner.dto.UserAdminRequest;
import com.example.events_planner.dto.UserDetailDTO;
import com.example.events_planner.dto.UserSummaryDTO;
import com.example.events_planner.entity.User;
import com.example.events_planner.exception.ResourceNotFoundException;
import com.example.events_planner.mapper.UserMapper;
import com.example.events_planner.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public User getCurrentUser() {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public List<UserDetailDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDetailDTO)
                .toList();
    }

    public List<UserSummaryDTO> getAllUserSummaries() {
        return userRepository.findAll().stream()
                .map(userMapper::toSummaryDTO)
                .toList();
    }

    public Optional<UserDetailDTO> getUserById(UUID id) {
        return userRepository.findById(id).map(userMapper::toDetailDTO);
    }

    public UserDetailDTO createUser(UserAdminRequest request) {
        log.info("Creating new user with username: {}", request.username());
        validateCreateUserRequest(request);

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(request.enabled() != null ? request.enabled() : true);
        user.setAccountNonLocked(request.accountNonLocked() != null ? request.accountNonLocked() : true);
        user.setAvatar(request.avatar());
        user.setAuthorities(request.authorities());
        
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        
        return userMapper.toDetailDTO(userRepository.save(user));
    }

    private void validateCreateUserRequest(UserAdminRequest request) {
        if (request.username() == null || request.username().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (request.email() != null && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (request.password() == null || request.password().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    public UserDetailDTO updateUser(UUID id, UserAdminRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        user.setUsername(request.username());
        user.setEmail(request.email());
        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }
        if (request.accountNonLocked() != null) {
            user.setAccountNonLocked(request.accountNonLocked());
        }
        user.setAvatar(request.avatar());
        if (request.authorities() != null) {
            user.setAuthorities(request.authorities());
        }
        user.setUpdatedAt(OffsetDateTime.now());

        if (request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        return userMapper.toDetailDTO(userRepository.save(user));
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
