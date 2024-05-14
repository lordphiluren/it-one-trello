package ru.sushchenko.trelloclone.service;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import ru.sushchenko.trelloclone.dto.ResponseMessage;
import ru.sushchenko.trelloclone.dto.attachments.AttachmentResponse;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistRequest;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.dto.comment.CommentRequest;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.dto.task.TaskFilterRequest;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.entity.enums.Priority;
import ru.sushchenko.trelloclone.entity.enums.Status;
import ru.sushchenko.trelloclone.repo.CommentRepo;
import ru.sushchenko.trelloclone.security.UserPrincipal;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TaskService {
    List<TaskResponse> getAllTasks();
    List<TaskResponse> getAllTasksWithFilters(TaskFilterRequest taskFilterRequest);
    TaskResponse getTaskById(UUID id);
    TaskResponse addTask(TaskRequest taskRequest, User creator);
    TaskResponse updateTaskById(UUID id, TaskRequest taskDto, User currentUser);
    TaskResponse addExecutorToTaskById(UUID id, UUID executorId, User currentUser);
    void removeExecutorFromTaskById(UUID id, UUID executorId, User currentUser);
    CommentResponse addCommentToTaskById(UUID id, CommentRequest commentRequest, User currentUser);
    List<AttachmentResponse> addAttachmentsToTaskById(UUID id, List<MultipartFile> attachments, User currentUser);
    ChecklistResponse addChecklistToTaskById(UUID id, ChecklistRequest checklistRequest, User currentUser);
    List<CommentResponse> getCommentsByTaskId(UUID id);
    List<AttachmentResponse> getAttachmentsByTaskId(UUID id);
    List<ChecklistResponse> getChecklistsByTaskId(UUID id);
    void deleteChecklistById(UUID id, UUID checklistId, User currentUser);
    void removeAttachmentFromTaskById(UUID id, UUID attachmentId, User currentUser);
    void deleteTaskById(UUID id, User creator);
    ChecklistResponse updateChecklistById(UUID id, UUID checklistId, ChecklistRequest checklistDto, User user);
}
