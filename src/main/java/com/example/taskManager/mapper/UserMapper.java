package com.example.taskManager.mapper;

import com.example.taskManager.dto.requests.RegisterRequest;
import com.example.taskManager.dto.responses.UserResponse;
import com.example.taskManager.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(RegisterRequest request, String passwordHash) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordHash)
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

}
