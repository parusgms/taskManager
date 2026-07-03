package com.example.taskManager.service;

import com.example.taskManager.dto.requests.GroupRequest;
import com.example.taskManager.dto.responses.GroupMemberResponse;
import com.example.taskManager.dto.responses.GroupResponse;
import com.example.taskManager.entity.Group;
import com.example.taskManager.entity.GroupMember;
import com.example.taskManager.entity.GroupMemberId;
import com.example.taskManager.entity.User;
import com.example.taskManager.entity.enums.GroupRole;
import com.example.taskManager.exception.types.*;
import com.example.taskManager.mapper.GroupMapper;
import com.example.taskManager.repository.GroupMemberRepository;
import com.example.taskManager.repository.GroupRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;

    @Transactional
    public GroupResponse createGroup(GroupRequest request, String username) {
        User owner = checkUser(username);
        Group group = groupMapper.toGroup(request, owner);
        Group savedGroup = groupRepository.save(group);

        GroupMember member = groupMapper.toMember(savedGroup, owner, GroupRole.OWNER);
        groupMemberRepository.save(member);

        return groupMapper.toResponse(savedGroup);
    }

    @Transactional
    public GroupResponse updateGroup(UUID groupId, GroupRequest request, String username) {
        User user = checkUser(username);
        Group group = checkGroup(groupId);
        checkIsOwner(group, user);

        groupMapper.updateGroup(group, request);
        Group updatedGroup = groupRepository.save(group);

        return groupMapper.toResponse(updatedGroup);
    }

    @Transactional
    public void deleteGroup(UUID groupId, String username) {
        User user = checkUser(username);
        Group group = checkGroup(groupId);
        checkIsOwner(group, user);

        List<GroupMember> members = groupRepository.findAllMembersByGroup(groupId);
        groupMemberRepository.deleteAll(members);
        groupRepository.delete(group);
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroup(UUID groupId, String username) {
        User user = checkUser(username);
        Group group = checkGroup(groupId);
        checkIsMember(groupId, user);
        return groupMapper.toResponse(group);
    }

    @Transactional(readOnly = true)
    public Page<GroupResponse> getUserGroups(String username, Pageable pageable) {
        User user = checkUser(username);
        Page<Group> groups = groupRepository.findAllGroupsByUser(user, pageable);
        return groups.map(groupMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<GroupMemberResponse> getGroupMembers(UUID groupId, String username, Pageable pageable) {
        User user = checkUser(username);
        checkGroup(groupId);
        checkIsMember(groupId, user);

        Page<GroupMember> members = groupRepository.findAllMembersByGroup(groupId, pageable);
        return members.map(groupMapper::toMemberResponse);
    }

    @Transactional
    public GroupMemberResponse addGroupMember(UUID groupId, String username, UUID userIdToAdd) {
        User user = checkUser(username);
        Group group = checkGroup(groupId);
        checkIsOwner(group, user);

        User newMember = userRepository.findById(userIdToAdd)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (groupRepository.isUserMemberOfGroup(groupId, newMember.getId())) {
            throw new DuplicateResourceException("Пользователь уже состоит в группе");
        }

        GroupMember member = groupMapper.toMember(group, newMember, GroupRole.MEMBER);
        GroupMember savedMember = groupMemberRepository.save(member);

        return groupMapper.toMemberResponse(savedMember);
    }

    @Transactional
    public void removeGroupMember(UUID groupId, String username, UUID userIdToRemove) {
        User user = checkUser(username);
        Group group = checkGroup(groupId);
        checkIsOwner(group, user);

        if (group.getOwner().getId().equals(userIdToRemove)) {
            throw new PermissionDeniedException("Нельзя удалить владельца группы");
        }

        GroupMemberId memberId = groupMapper.toMemberId(groupId, userIdToRemove);

        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден в группе"));

        groupMemberRepository.delete(member);
    }



    private User checkUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    private Group checkGroup(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
    }

    private void checkIsOwner(Group group, User user) {
        if (!group.getOwner().getId().equals(user.getId())) {
            throw new PermissionDeniedException("Только владелец группы может выполнить это действие");
        }
    }

    private void checkIsMember(UUID groupId, User user) {
        if (!groupRepository.isUserMemberOfGroup(groupId, user.getId())) {
            throw new PermissionDeniedException("Вы не состоите в этой группе");
        }
    }
}