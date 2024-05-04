package ru.sushchenko.trelloclone.service;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import ru.sushchenko.trelloclone.dto.attachments.AttachmentResponse;
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
    List<TaskResponse> getAllTasks(TaskFilterRequest taskFilterRequest);
    TaskResponse getTaskById(UUID id);
    TaskResponse addTask(TaskRequest taskRequest, User creator);
    TaskResponse updateTaskById(UUID id, TaskRequest taskDto, User currentUser);
    CommentResponse addCommentToTaskById(UUID id, CommentRequest commentRequest, User currentUser);
    List<AttachmentResponse> addAttachmentsToTaskById(UUID id, List<MultipartFile> attachments, User currentUser);
    List<CommentResponse> getCommentsByTaskId(UUID id);
    void deleteTaskById(UUID id, User creator);
}
