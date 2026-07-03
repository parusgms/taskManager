package com.example.taskManager.controller;

import com.example.taskManager.dto.requests.TaskRequest;
import com.example.taskManager.dto.requests.UpdateTaskStatusRequest;
import com.example.taskManager.dto.responses.TaskResponse;
import com.example.taskManager.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(HttpServletRequest request, @Valid @RequestBody TaskRequest taskRequest) {
        String username = (String) request.getAttribute("username");
        TaskResponse createdTask = taskService.createTask(taskRequest, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(HttpServletRequest request, @Valid @RequestBody TaskRequest taskRequest, @PathVariable UUID id) {
        String username = (String) request.getAttribute("username");
        TaskResponse updatedTask = taskService.updateTask(taskRequest, username, id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(HttpServletRequest request, @PathVariable UUID id) {
        String username = (String) request.getAttribute("username");
        taskService.deleteTask(id, username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(HttpServletRequest request, @PathVariable UUID id) {
        String username = (String) request.getAttribute("username");
        TaskResponse task = taskService.getTask(id, username);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(HttpServletRequest request, @Valid @PathVariable UUID id, @RequestBody UpdateTaskStatusRequest statusRequest) {
        String username = (String) request.getAttribute("username");
        TaskResponse updatedTask = taskService.updateTaskStatus(id, username, statusRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID assignee,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) String priority
    ) {
        String username = (String) request.getAttribute("username");
        Page<TaskResponse> tasks = taskService.getTasks(username, page, size, status, assignee, dueDate, priority);
        return ResponseEntity.ok(tasks);
    }
}