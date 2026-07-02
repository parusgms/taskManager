package com.example.taskManager.dto.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddMemberRequest(
        @NotNull(message = "ID пользователя обязателен")
        UUID id
) {
}