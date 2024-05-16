package ru.sushchenko.trelloclone.service;

import org.springframework.web.multipart.MultipartFile;
import ru.sushchenko.trelloclone.dto.attachments.AttachmentResponse;
import ru.sushchenko.trelloclone.dto.comment.CommentRequest;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.dto.task.TaskFilterRequest;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskResponse> getAllTasks();
    List<TaskResponse> getAllTasksWithFilters(TaskFilterRequest taskFilterRequest);
    TaskResponse getTaskById(UUID id);
    TaskResponse addTask(TaskRequest taskRequest, User creator);
    TaskResponse updateTaskById(UUID id, TaskRequest taskDto, User currentUser);
    TaskResponse addExecutorToTaskById(UUID id, UUID executorId, User currentUser);
    void removeExecutorFromTaskById(UUID id, UUID executorId, User currentUser);
    void deleteTaskById(UUID id, User creator);
    void validatePermissions(Task task, User currentUser);
    void validateOwnership(Task task, User currentUser);
    Task getExistingTask(UUID id);
}
