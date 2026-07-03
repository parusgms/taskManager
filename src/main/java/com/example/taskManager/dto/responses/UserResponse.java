package com.example.taskManager.dto.responses;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse (
        UUID id,
        String username,
        String email
){
}
