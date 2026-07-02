package com.example.taskManager.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
        @NotBlank(message = "Имя пользователя обязательно")
        @Size(min = 5, max = 16, message = "Имя пользователя должно содержать от 5 до 16 символов")
        String username,

        @NotBlank(message = "Почта обязательна")
        @Email(message = "Неверный формат почты")
        String email,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, max = 32, message = "Пароль должен содержать от 8 до 32 символов")
        String password
){
}
