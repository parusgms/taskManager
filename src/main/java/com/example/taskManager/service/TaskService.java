package com.example.taskManager.service;

import com.example.taskManager.dto.requests.TaskRequest;
import com.example.taskManager.dto.requests.UpdateTaskStatusRequest;
import com.example.taskManager.dto.responses.TaskResponse;
import com.example.taskManager.entity.Group;
import com.example.taskManager.entity.Task;
import com.example.taskManager.entity.User;
import com.example.taskManager.entity.enums.TaskPriority;
import com.example.taskManager.entity.enums.TaskStatus;
import com.example.taskManager.exception.types.*;
import com.example.taskManager.mapper.TaskMapper;
import com.example.taskManager.repository.GroupRepository;
import com.example.taskManager.repository.TaskRepository;
import com.example.taskManager.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponse createTask(TaskRequest request, String username) {
        User author = checkUser(username);
        Group group = checkGroup(request.groupId());
        isMemberOfGroup(group.getId(), author.getId(), "Вы не являетесь членом данной группы");

        User assignee = resolveAssignee(request.assigneeId(), group.getId());

        Task task = taskMapper.toTask(request, assignee, author, group, TaskStatus.NEW, parsePriority(request.priority()));
        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(TaskRequest request, String username, UUID taskId) {
        User author = checkUser(username);
        Task task = checkTask(taskId);
        isTaskAuthorOrOwner(task, author.getId(), "Только владелец группы или автор может обновить данную задачу");

        User assignee = resolveAssignee(request.assigneeId(), task.getGroup().getId());

        taskMapper.updateTask(task, request, assignee, parseStatus(request.status()), parsePriority(request.priority()));
        Task updatedTask = taskRepository.save(task);

        return taskMapper.toResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(UUID taskId, String username) {
        User author = checkUser(username);
        Task task = checkTask(taskId);
        isTaskAuthorOrOwner(task, author.getId(), "Только владелец группы или автор может удалить данную задачу");

        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID taskId, String username) {
        User user = checkUser(username);
        Task task = checkTask(taskId);
        isMemberOfGroup(task.getGroup().getId(), user.getId(), "Вы не являетесь членом данной группы");
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse updateTaskStatus(UUID taskId, String username, UpdateTaskStatusRequest request) {
        User user = checkUser(username);
        Task task = checkTask(taskId);
        isMemberOfGroup(task.getGroup().getId(), user.getId(), "Вы не являетесь членом данной группы");

        task.setStatus(parseStatus(request.status()));
        Task updatedTask = taskRepository.save(task);

        return taskMapper.toResponse(updatedTask);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasks(String username, int page, int size, String status,
                                       UUID assigneeId, LocalDate dueDate, String priority) {
        User user = checkUser(username);
        List<Group> groups = groupRepository.findAllGroupsByUser(user);
        if (groups.isEmpty()) {
            return Page.empty();
        }

        List<UUID> groupIds = groups.stream().map(Group::getId).toList();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Task> groupSpec = (root, query, cb) -> root.get("group").get("id").in(groupIds);
        Specification<Task> specification = Specification.where(groupSpec)
                .and(getTaskSpecification(status, assigneeId, dueDate, priority));

        Page<Task> taskPage = taskRepository.findAll(specification, pageable);
        return taskPage.map(taskMapper::toResponse);
    }

    private User resolveAssignee(UUID assigneeId, UUID groupId) {
        if (assigneeId == null) {
            return null;
        }
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        isMemberOfGroup(groupId, assignee.getId(), "Исполнитель не является членом данной группы");
        return assignee;
    }

    private TaskStatus parseStatus(String status) {
        try {
            return TaskStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException("Некорректное значение статуса");
        }
    }

    private TaskPriority parsePriority(String priority) {
        try {
            return TaskPriority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException("Некорректное значение приоритета");
        }
    }

    private User checkUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    private Group checkGroup(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
    }

    private Task checkTask(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Задача не найдена"));
    }

    private void isMemberOfGroup(UUID groupId, UUID userId, String message) {
        if (!groupRepository.isUserMemberOfGroup(groupId, userId)) {
            throw new PermissionDeniedException(message);
        }
    }

    private void isTaskAuthorOrOwner(Task task, UUID userId, String message) {
        boolean isOwner = task.getGroup().getOwner().getId().equals(userId);
        boolean isAuthor = task.getAuthor().getId().equals(userId);
        if (!isOwner && !isAuthor) {
            throw new PermissionDeniedException(message);
        }
    }

    private Specification<Task> getTaskSpecification(String status, UUID assignee, LocalDate dueDate, String priority) {
        return (root, query, cb) -> {
            var criteria = Stream.of(
                    Optional.ofNullable(status).filter(Strings::isNotBlank)
                            .map(v -> cb.equal(root.get("status"), parseStatus(v))),
                    Optional.ofNullable(assignee)
                            .map(v -> cb.equal(root.get("assignee").get("id"), v)),
                    Optional.ofNullable(dueDate)
                            .map(v -> cb.equal(root.get("dueDate"), v)),
                    Optional.ofNullable(priority).filter(Strings::isNotBlank)
                            .map(v -> cb.equal(root.get("priority"), parsePriority(v)))
            ).filter(Optional::isPresent).map(Optional::get).toArray(Predicate[]::new);

            return cb.and(criteria);
        };
    }
}