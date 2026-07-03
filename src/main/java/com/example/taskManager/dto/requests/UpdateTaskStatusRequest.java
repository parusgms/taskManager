package com.example.taskManager.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record UpdateTaskStatusRequest (
        @NotBlank(message = "Статус обязателен")
        String status
){
}
