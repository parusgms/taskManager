package com.example.taskManager.dto.responses;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record GroupResponse(
        UUID id,
        String name,
        String description,
        UserResponse owner,
        LocalDateTime createdAt
) {
}