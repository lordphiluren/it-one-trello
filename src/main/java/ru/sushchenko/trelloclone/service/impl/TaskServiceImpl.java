package ru.sushchenko.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sushchenko.trelloclone.dto.attachments.AttachmentResponse;
import ru.sushchenko.trelloclone.dto.comment.CommentRequest;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.dto.task.TaskFilterRequest;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.dto.task.TaskStatusRequest;
import ru.sushchenko.trelloclone.entity.Tag;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.entity.enums.Status;
import ru.sushchenko.trelloclone.entity.id.TaskTagKey;
import ru.sushchenko.trelloclone.repo.TaskRepo;
import ru.sushchenko.trelloclone.repo.spec.TaskSpecification;
import ru.sushchenko.trelloclone.service.*;
import ru.sushchenko.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.sushchenko.trelloclone.utils.exception.ResourceMismatchException;
import ru.sushchenko.trelloclone.utils.exception.ResourceNotFoundException;
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
    private final TagService tagService;

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepo.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getAllTasksWithFilters(TaskFilterRequest taskFilter) {
        List<Task> task;
        if(taskFilter != null) {
            Specification<Task> spec = getSpecificationFromFilter(taskFilter);
            Pageable pageable = getPageableFromFilter(taskFilter);
            task = taskRepo.findAll(spec, pageable).getContent();
        } else {
            task = taskRepo.findAll();
        }
        return task.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse getTaskById(UUID id) {
        return taskMapper.toDto(getExistingTask(id));
    }

    @Override
    @Transactional
    public TaskResponse addTask(TaskRequest taskDto, User creator) {
        Task task = createTaskFromDto(taskDto);

        task.setCreator(creator);
        task.setExecutors(createExecutorsFromDto(taskDto));
        task.setTags(createTagsFromDto(taskDto, task));

        Task savedTask = taskRepo.save(task);
        log.info("Task with id: {} created", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskById(UUID id, TaskRequest taskDto, User currentUser) {
        Task task = getExistingTask(id);

        validatePermissions(task, currentUser);

        task.setUpdatedAt(new Date());
        task.setClosedAt(updateClosedAt(task));
        task.setExecutors(createExecutorsFromDto(taskDto));
        task.setTags(createTagsFromDto(taskDto, task));

        taskMapper.mergeDtoIntoEntity(taskDto, task);

        Task savedTask = taskRepo.save(task);
        tagService.deleteTagsByTask(savedTask);
        log.info("Task with id: {} updated by user with id: {}", savedTask.getId(), currentUser.getId());
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse addExecutorToTaskById(UUID id, UUID executorId, User currentUser) {
        Task task = getExistingTask(id);

        validateOwnership(task, currentUser);

        User executor = userService.getExistingUser(executorId);
        Set<User> executorsToUpdate = new HashSet<>(task.getExecutors());
        executorsToUpdate.add(executor);

        task.setExecutors(executorsToUpdate);
        Task savedTask = taskRepo.save(task);
        log.info("Task with id: {} updated by user with id: {}", savedTask.getId(), currentUser.getId());
        return taskMapper.toDto(savedTask);

    }

    @Override
    @Transactional
    public void removeExecutorFromTaskById(UUID id, UUID executorId, User currentUser) {
        Task task = getExistingTask(id);

        validateOwnership(task, currentUser);

        Set<User> executorsToUpdate = new HashSet<>(task.getExecutors());
        boolean isRemoved = executorsToUpdate.removeIf(e -> e.getId().equals(executorId));

        if(isRemoved) {
            task.setExecutors(executorsToUpdate);
            Task savedTask = taskRepo.save(task);
            log.info("Executor with id: {} removed from task with id: {} by user with id: {}", executorId,
                    savedTask.getId(), currentUser.getId());
        } else {
            throw new ResourceMismatchException("Executor with id: " + executorId +
                    " doesn't belong to task with id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteTaskById(UUID id, User currentUser) {
        Task task = getExistingTask(id);

        validateOwnership(task, currentUser);

        taskRepo.deleteById(id);
        log.info("Task with id: {} deleted by user with id: {}", id, currentUser.getId());
    }

    @Override
    public void validatePermissions(Task task, User currentUser) {
        if(!checkIfCreator(task, currentUser) && !checkIfExecutor(task, currentUser)) {
            throw new NotEnoughPermissionsException(currentUser.getId(), task.getId());
        }
    }

    @Override
    public void validateOwnership(Task task, User currentUser) {
        if(!checkIfCreator(task, currentUser)) {
            throw new NotEnoughPermissionsException(currentUser.getId(), task.getId());
        }
    }

    @Override
    public Task getExistingTask(UUID id) {
        return taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatusById(UUID id, TaskStatusRequest taskStatusRequest, User currentUser) {
        Task task = getExistingTask(id);

        validatePermissions(task, currentUser);

        task.setStatus(taskStatusRequest.getStatus());
        task.setUpdatedAt(new Date());
        task.setClosedAt(updateClosedAt(task));

        Task savedTask = taskRepo.save(task);
        log.info("Status for task with id: {} changed for - {}", savedTask.getId(), taskStatusRequest.getStatus());

        return taskMapper.toDto(savedTask);
    }

    private Pageable getPageableFromFilter(TaskFilterRequest taskFilter) {
        int index = taskFilter.getPageIndex() != null ? taskFilter.getPageIndex() : 0;
        int size = taskFilter.getPageSize() != null ? taskFilter.getPageSize() : 50;
        String sort = taskFilter.getSort() != null ? taskFilter.getSort().getValue() : null;
        if(sort != null) {
            return PageRequest.of(index, size, Sort.by(sort));
        } else {
            return PageRequest.of(index, size);
        }
    }

    private Specification<Task> getSpecificationFromFilter(TaskFilterRequest taskFilter) {
        return TaskSpecification.filterTasks(taskFilter.getPriority(), taskFilter.getStatus(),
                taskFilter.getTags(), taskFilter.getCreatorId(), taskFilter.getEndDate());
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

    private Date updateClosedAt(Task task) {
        return task.getStatus() == Status.DONE ? new Date() : null;
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
        task.setChecklists(new HashSet<>());
        task.setCommentsCount(0L);
        task.setAttachmentsCount(0L);
        task.setCheckItemsCount(0L);
        task.setCheckItemsCheckedCount(0L);
    }

    private Task createTaskFromDto(TaskRequest taskDto) {
        Task task = taskMapper.toEntity(taskDto);
        enrichTask(task);
        return task;
    }

    private boolean checkIfCreator(Task task, User currentUser) {
        return Objects.equals(task.getCreator().getId(), currentUser.getId());
    }

    private boolean checkIfExecutor(Task task, User currentUser) {
        return task.getExecutors().stream()
                .anyMatch(executor -> Objects.equals(executor.getId(), currentUser.getId()));
    }
}
