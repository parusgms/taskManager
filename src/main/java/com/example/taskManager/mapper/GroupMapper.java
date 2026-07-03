package com.example.taskManager.mapper;

import com.example.taskManager.dto.requests.GroupRequest;
import com.example.taskManager.dto.responses.GroupMemberResponse;
import com.example.taskManager.dto.responses.GroupResponse;
import com.example.taskManager.entity.Group;
import com.example.taskManager.entity.GroupMember;
import com.example.taskManager.entity.GroupMemberId;
import com.example.taskManager.entity.User;
import com.example.taskManager.entity.enums.GroupRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupMapper {

    private final UserMapper userMapper;

    public Group toGroup(GroupRequest request, User owner) {
        return Group.builder()
                .name(request.name())
                .description(request.description())
                .owner(owner)
                .build();
    }

    public void updateGroup(Group group, GroupRequest request) {
        group.setName(request.name());
        group.setDescription(request.description());
    }

    public GroupMember toMember(Group group, User user, GroupRole role) {
        GroupMemberId id = GroupMemberId.builder()
                .groupId(group.getId())
                .userId(user.getId())
                .build();

        return GroupMember.builder()
                .id(id)
                .group(group)
                .user(user)
                .role(role)
                .build();
    }


    public GroupMemberId toMemberId(UUID groupId, UUID userId) {
        return GroupMemberId.builder()
                .groupId(groupId)
                .userId(userId)
                .build();
    }

    public GroupResponse toResponse(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .owner(userMapper.toResponse(group.getOwner()))
                .createdAt(group.getCreatedAt())
                .build();
    }

    public GroupMemberResponse toMemberResponse(GroupMember member) {
        return GroupMemberResponse.builder()
                .user(userMapper.toResponse(member.getUser()))
                .role(member.getRole())
                .build();
    }
}