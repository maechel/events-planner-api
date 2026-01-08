package com.example.events_planner.web;

import com.example.events_planner.entity.User;
import com.example.events_planner.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

@SpringBootTest
public abstract class BaseWebTest {

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected UserRepository userRepository;

    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    protected User createAndSaveUser(String username, String password, String... roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setAuthorities(Set.of(roles));
        return userRepository.save(user);
    }
}
