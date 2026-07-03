package com.example.taskManager.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record TaskRequest (
        @NotBlank(message = "Название задачи обязательно")
        @Size(max = 200, message = "Название должно быть не более 200 символов")
        String title,

        String description,
        String status,

        @NotBlank(message = "Приоритет обязателен")
        String priority,

        LocalDate dueDate,
        UUID assigneeId,

        @NotNull(message = "ID группы обязателен")
        UUID groupId
){
}
