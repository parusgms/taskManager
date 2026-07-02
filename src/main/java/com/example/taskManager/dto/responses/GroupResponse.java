package com.example.taskManager.dto.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupResponse(
        UUID id,
        String name,
        String description,
        UserResponse owner,
        LocalDateTime createdAt
) {
}