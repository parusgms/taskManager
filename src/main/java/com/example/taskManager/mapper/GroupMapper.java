package com.example.taskManager.mapper;

import com.example.taskManager.dto.requests.GroupRequest;
import com.example.taskManager.dto.responses.GroupMemberResponse;
import com.example.taskManager.dto.responses.GroupResponse;
import com.example.taskManager.entity.Group;
import com.example.taskManager.entity.GroupMember;
import com.example.taskManager.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

    public GroupResponse toResponse(Group group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                userMapper.toResponse(group.getOwner()),
                group.getCreatedAt()
        );
    }

    public GroupMemberResponse toMemberResponse(GroupMember member) {
        return new GroupMemberResponse(
                userMapper.toResponse(member.getUser()),
                member.getRole()
        );
    }
}