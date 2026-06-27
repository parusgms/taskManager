package com.example.taskManager.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
public class GroupMemberId implements Serializable {

    private UUID groupId;
    private UUID userId;
}
