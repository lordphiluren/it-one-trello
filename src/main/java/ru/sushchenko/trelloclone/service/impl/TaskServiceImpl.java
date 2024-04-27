package ru.sushchenko.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.entity.Tag;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.entity.enums.Status;
import ru.sushchenko.trelloclone.entity.id.TaskTagKey;
import ru.sushchenko.trelloclone.repo.TaskRepo;
import ru.sushchenko.trelloclone.service.TaskService;
import ru.sushchenko.trelloclone.service.UserService;
import ru.sushchenko.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.sushchenko.trelloclone.utils.exception.TaskNotFoundException;
import ru.sushchenko.trelloclone.utils.mapper.TaskMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepo taskRepo;
    private final TaskMapper taskMapper;
    private final UserService userService;
    @Override
    @Transactional
    public List<TaskResponse> getAllTasks() {
        return taskRepo.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse getTaskById(UUID id) {
        return taskMapper.toDto(getExistingTask(id));
    }

    @Override
    @Transactional
    public TaskResponse addTask(TaskRequest taskDto, User creator) {
        Task task = createTaskFromDto(taskDto);
        task.setCreator(creator);
        task.setTags(createTagsFromDto(taskDto, task));
        task.setExecutors(createExecutorsFromDto(taskDto));
        Task savedTask = taskRepo.save(task);
        log.info("Task with id: {} created", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskById(UUID id, TaskRequest taskDto, User currentUser) {
        Task task = getExistingTask(id);
        if(checkIfAllowedToModifyTask(task, currentUser)) {
            taskMapper.mergeDtoIntoEntity(taskDto, task);
            if(task.getStatus() == Status.DONE) {
                task.setClosedAt(new Date());
            } else {
                task.setClosedAt(null);
            }
            task.setUpdatedAt(new Date());
            task.setExecutors(createExecutorsFromDto(taskDto));
            task.setTags(createTagsFromDto(taskDto, task));
            Task savedTask = taskRepo.saveAndFlush(task);
            log.info("Task with id: {} updated by user with id: {}", savedTask.getId(), currentUser.getId());
            return taskMapper.toDto(savedTask);
        } else {
            log.warn("User with id: {} tried to modify task with id: {}", currentUser.getId(), task.getId());
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't modify task with id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteTaskById(UUID id, User currentUser) {
        Task task = getExistingTask(id);
        if(checkIfCreator(task, currentUser)) {
            taskRepo.deleteById(id);
            log.info("Task with id: {} deleted by user with id: {}", id, currentUser.getId());
        } else {
            log.warn("User with id: {} tried to delete task with id: {}", currentUser.getId(), task.getId());
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't delete task with id: " + id);
        }
    }
    private Task getExistingTask(UUID id) {
        return taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
    private Set<Tag> createTagsFromDto(TaskRequest taskDto, Task task) {
        return taskDto.getTags().stream()
                .map(tag -> {
                    TaskTagKey tagKey = new TaskTagKey(task.getId(), tag);
                    Tag newTag = new Tag();
                    newTag.setId(tagKey);
                    newTag.setTask(task);
                    return newTag;
                })
                .collect(Collectors.toSet());
    }
    private Set<User> createExecutorsFromDto(TaskRequest taskDto) {
        return userService.getUsersByIdIn(taskDto.getExecutorIds());
    }
    private void enrichTask(Task task) {
        task.setCreatedAt(new Date());
        task.setExecutors(new HashSet<>());
        task.setComments(new HashSet<>());
        task.setAttachments(new HashSet<>());
        task.setTags(new HashSet<>());
    }
    private Task createTaskFromDto(TaskRequest taskDto) {
        Task task = taskMapper.toEntity(taskDto);
        enrichTask(task);
        return task;
    }
    private boolean checkIfAllowedToModifyTask(Task task, User currentUser) {
        return checkIfCreator(task, currentUser) || checkIfExecutor(task, currentUser);
    }
    private boolean checkIfCreator(Task task, User currentUser) {
        return Objects.equals(task.getCreator().getId(), currentUser.getId());
    }
    private boolean checkIfExecutor(Task task, User currentUser) {
         return task.getExecutors().stream()
                 .map(User::getId)
                 .collect(Collectors.toSet())
                 .contains(currentUser.getId());
    }
}
