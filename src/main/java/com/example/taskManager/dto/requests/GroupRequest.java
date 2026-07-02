package com.example.taskManager.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupRequest(
        @NotBlank(message = "Название группы обязательно")
        @Size(max = 100, message = "Название должно быть не более 100 символов")
        String name,

        String description
){
}