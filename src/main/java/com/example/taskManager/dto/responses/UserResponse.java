package com.example.taskManager.dto.responses;

import java.util.UUID;

public record UserResponse (
        UUID id,
        String username,
        String email
){
}
