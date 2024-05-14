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
import ru.sushchenko.trelloclone.dto.checklist.ChecklistRequest;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.dto.comment.CommentRequest;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.dto.task.TaskFilterRequest;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.entity.Tag;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.entity.enums.Status;
import ru.sushchenko.trelloclone.entity.id.TaskTagKey;
import ru.sushchenko.trelloclone.repo.ChecklistRepo;
import ru.sushchenko.trelloclone.repo.TagRepo;
import ru.sushchenko.trelloclone.repo.TaskRepo;
import ru.sushchenko.trelloclone.repo.spec.TaskSpecification;
import ru.sushchenko.trelloclone.service.*;
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
    private final TagService tagService;
    private final CommentService commentService;
    private final AttachmentService attachService;
    private final ChecklistService checklistService;

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
        if(checkIfAllowedToModifyTask(task, currentUser)) {
            task.setUpdatedAt(new Date());
            task.setClosedAt(updateClosedAt(task));
            task.setExecutors(createExecutorsFromDto(taskDto));
            task.setTags(createTagsFromDto(taskDto, task));
            taskMapper.mergeDtoIntoEntity(taskDto, task);
            Task savedTask = taskRepo.save(task);
            tagService.deleteTagsByTask(savedTask);
            log.info("Task with id: {} updated by user with id: {}", savedTask.getId(), currentUser.getId());
            return taskMapper.toDto(savedTask);
        } else {
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't modify task with id: " + id);
        }
    }

    @Override
    @Transactional
    public TaskResponse addExecutorToTaskById(UUID id, UUID executorId, User currentUser) {
        Task task = getExistingTask(id);
        if(checkIfCreator(task, currentUser)) {
            User executor = userService.getExistingUser(executorId);
            task.getExecutors().add(executor);
            Task savedTask = taskRepo.save(task);
            log.info("Task with id: {} updated by user with id: {}", savedTask.getId(), currentUser.getId());
            return taskMapper.toDto(savedTask);
        } else {
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't modify task with id: " + id);
        }
    }
    @Override
    @Transactional
    public void removeExecutorFromTaskById(UUID id, UUID executorId, User currentUser) {
        Task task = getExistingTask(id);
        if(checkIfCreator(task, currentUser)) {
            User executor = userService.getExistingUser(executorId);
            task.getExecutors().removeIf(e -> e.getId().equals(executor.getId()));
            Task savedTask = taskRepo.save(task);
            log.info("Executor with id: {} removed from task with id: {} by user with id: {}", executorId,
                    savedTask.getId(), currentUser.getId());
        } else {
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't modify task with id: " + id);
        }
    }

    @Override
    @Transactional
    public CommentResponse addCommentToTaskById(UUID id, CommentRequest commentDto, User currentUser) {
        Task task = getExistingTask(id);
        if(checkIfAllowedToModifyTask(task, currentUser)) {
            return commentService.addComment(commentDto, task, currentUser);
        } else {
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't write comments in task with id: " + id);
        }
    }

    @Override
    @Transactional
    public List<AttachmentResponse> addAttachmentsToTaskById(UUID id, List<MultipartFile> attachments, User currentUser) {
        Task task = getExistingTask(id);
        if(checkIfAllowedToModifyTask(task, currentUser)) {
            List<AttachmentResponse> savedAttachments =  attachService.addAttachmentsToTask(task, attachments);
            log.info("{} attachments saved for task with id: {} by user with id: {}", savedAttachments.size(),
                    task.getId(), currentUser.getId());
            return savedAttachments;
        } else {
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't add attachments to task with id: " + id);
        }
    }

    @Override
    @Transactional
    public ChecklistResponse addChecklistToTaskById(UUID id, ChecklistRequest checklistDto, User currentUser) {
        Task task = getExistingTask(id);
        if(checkIfAllowedToModifyTask(task, currentUser)) {
            ChecklistResponse savedChecklist = checklistService.addChecklist(checklistDto, task);
            log.info("Checklist with id: {} created for task with id: {}", savedChecklist.getId(), task.getId());
            return savedChecklist;
        } else {
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't add checklists to task with id: " + id);
        }
    }

    @Override
    public List<CommentResponse> getCommentsByTaskId(UUID id) {
        return commentService.getCommentsByTaskId(id);
    }

    @Override
    public List<AttachmentResponse> getAttachmentsByTaskId(UUID id) {
        return attachService.getAttachmentsByTaskId(id);
    }

    @Override
    public List<ChecklistResponse> getChecklistsByTaskId(UUID id) {
        return checklistService.getChecklistsByTaskId(id);
    }

    @Override
    @Transactional
    public void deleteChecklistById(UUID id, UUID checklistId, User currentUser) {
        Task task = getExistingTask(id);
        if(checkIfAllowedToModifyTask(task, currentUser)) {
            checklistService.deleteChecklistById(checklistId);
            log.info("Checklist with id: {} deleted from task with id: {} by user with id: {}", checklistId, id,
                    currentUser.getId());
        } else {
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't modify task with id: " + id);
        }
    }

    @Override
    @Transactional
    public void removeAttachmentFromTaskById(UUID id, UUID attachmentId, User currentUser) {
        Task task = getExistingTask(id);
        if(checkIfAllowedToModifyTask(task, currentUser)) {
            attachService.deleteAttachmentById(attachmentId);
            log.info("Attachment with id: {} deleted from task with id: {} by user with id: {}", attachmentId, id,
                    currentUser.getId());
        } else {
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't remove attachments from task with id: " + id);
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
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't delete task with id: " + id);
        }
    }

    @Override
    @Transactional
    public ChecklistResponse updateChecklistById(UUID id, UUID checklistId,
                                                 ChecklistRequest checklistDto, User currentUser) {
        Task task = getExistingTask(id);
        if(checkIfAllowedToModifyTask(task, currentUser)) {
            ChecklistResponse updatedChecklist = checklistService.updateChecklistById(checklistId, checklistDto);
            log.info("Checklist with id: {} updated by user with id: {}", checklistId, currentUser.getId());
            return updatedChecklist;
        } else {
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't add modify task with id: " + id);
        }
    }

    private Task getExistingTask(UUID id) {
        return taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
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

    private boolean checkIfAllowedToModifyTask(Task task, User currentUser) {
        return checkIfCreator(task, currentUser) || checkIfExecutor(task, currentUser);
    }

    private boolean checkIfCreator(Task task, User currentUser) {
        return Objects.equals(task.getCreator().getId(), currentUser.getId());
    }

    private boolean checkIfExecutor(Task task, User currentUser) {
        return task.getExecutors().stream()
                .anyMatch(executor -> Objects.equals(executor.getId(), currentUser.getId()));
    }
}
