package com.example.taskManager.dto.responses;

import com.example.taskManager.entity.enums.GroupRole;

import java.util.UUID;

public record GroupMemberResponse(
        UserResponse user,
        GroupRole role
) {
}