package com.example.taskManager.mapper;

import com.example.taskManager.dto.requests.TaskRequest;
import com.example.taskManager.dto.responses.TaskResponse;
import com.example.taskManager.entity.Group;
import com.example.taskManager.entity.Task;
import com.example.taskManager.entity.User;
import com.example.taskManager.entity.enums.TaskPriority;
import com.example.taskManager.entity.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final UserMapper userMapper;
    private final GroupMapper groupMapper;

    public Task toTask(TaskRequest request, User assignee, User author, Group group, TaskStatus status, TaskPriority priority) {
        return Task.builder()
                .title(request.title())
                .description(request.description())
                .status(status)
                .priority(priority)
                .dueDate(request.dueDate())
                .assignee(assignee)
                .author(author)
                .group(group)
                .build();
    }

    public void updateTask(Task task, TaskRequest request, User assignee, TaskStatus status, TaskPriority priority) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(request.dueDate());
        task.setAssignee(assignee);
    }

    public TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .dueDate(task.getDueDate())
                .assignee(userMapper.toResponse(task.getAssignee()))
                .group(groupMapper.toResponse(task.getGroup()))
                .author(userMapper.toResponse(task.getAuthor()))
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}