package com.example.taskManager.dto.responses;

import com.example.taskManager.entity.enums.GroupRole;
import lombok.Builder;

@Builder
public record GroupMemberResponse(
        UserResponse user,
        GroupRole role
) {
}