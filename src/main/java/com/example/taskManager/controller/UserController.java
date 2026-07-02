package com.example.taskManager.controller;

import com.example.taskManager.dto.responses.UserResponse;
import com.example.taskManager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return userService.getCurrentUser(username);
    }

    @GetMapping
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getAllUsers(page, size);
    }
}