package com.example.taskManager.controller;

import com.example.taskManager.dto.requests.AddMemberRequest;
import com.example.taskManager.dto.requests.GroupRequest;
import com.example.taskManager.dto.responses.GroupMemberResponse;
import com.example.taskManager.dto.responses.GroupResponse;
import com.example.taskManager.service.GroupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public GroupResponse createGroup(
            @Valid @RequestBody GroupRequest request,
            HttpServletRequest httpRequest
    ) {
        String username = (String) httpRequest.getAttribute("username");
        return groupService.createGroup(request, username);
    }

    @GetMapping
    public Page<GroupResponse> getUserGroups(
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String username = (String) httpRequest.getAttribute("username");
        Pageable pageable = PageRequest.of(page, size);
        return groupService.getUserGroups(username, pageable);
    }

    @GetMapping("/{id}")
    public GroupResponse getGroup(
            @PathVariable UUID id,
            HttpServletRequest httpRequest
    ) {
        String username = (String) httpRequest.getAttribute("username");
        return groupService.getGroup(id, username);
    }

    @PutMapping("/{id}")
    public GroupResponse updateGroup(
            @PathVariable UUID id,
            @Valid @RequestBody GroupRequest request,
            HttpServletRequest httpRequest
    ) {
        String username = (String) httpRequest.getAttribute("username");
        return groupService.updateGroup(id, request, username);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(
            @PathVariable UUID id,
            HttpServletRequest httpRequest
    ) {
        String username = (String) httpRequest.getAttribute("username");
        groupService.deleteGroup(id, username);
    }

    @PostMapping("/{id}/members")
    public GroupMemberResponse addMember(
            @PathVariable UUID id,
            @Valid @RequestBody AddMemberRequest request,
            HttpServletRequest httpRequest
    ) {
        String username = (String) httpRequest.getAttribute("username");
        return groupService.addGroupMember(id, username, request.id());
    }

    @DeleteMapping("/{id}/members/{userId}")
    public void removeMember(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            HttpServletRequest httpRequest
    ) {
        String username = (String) httpRequest.getAttribute("username");
        groupService.removeGroupMember(id, username, userId);
    }

    @GetMapping("/{groupId}/members")
    public Page<GroupMemberResponse> getGroupMembers(
            @PathVariable UUID groupId,
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String username = (String) httpRequest.getAttribute("username");
        Pageable pageable = PageRequest.of(page, size);
        return groupService.getGroupMembers(groupId, username, pageable);
    }
}