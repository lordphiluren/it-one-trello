package ru.sushchenko.trelloclone.service;

import org.springframework.data.domain.Pageable;
import ru.sushchenko.trelloclone.dto.task.TaskFilterRequest;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.entity.enums.Priority;
import ru.sushchenko.trelloclone.entity.enums.Status;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TaskService {
    List<TaskResponse> getAllTasks(TaskFilterRequest taskFilterRequest);
    TaskResponse getTaskById(UUID id);
    TaskResponse addTask(TaskRequest taskRequest, User creator);
    TaskResponse updateTaskById(UUID id, TaskRequest taskDto, User currentUser);
    void deleteTaskById(UUID id, User creator);
}
