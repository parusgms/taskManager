package com.example.taskManager.dto.responses;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TaskResponse (
        UUID id,
        String title,
        String description,
        String status,
        String priority,
        LocalDate dueDate,
        UserResponse assignee,
        GroupResponse group,
        UserResponse author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}
