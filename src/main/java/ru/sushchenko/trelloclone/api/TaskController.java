package ru.sushchenko.trelloclone.api;

import com.sun.source.doctree.CommentTree;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import ru.sushchenko.trelloclone.dto.attachments.AttachmentResponse;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistRequest;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.dto.comment.CommentRequest;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.dto.task.TaskFilterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.dto.task.TaskStatusRequest;
import ru.sushchenko.trelloclone.security.UserPrincipal;
import ru.sushchenko.trelloclone.service.AttachmentService;
import ru.sushchenko.trelloclone.service.ChecklistService;
import ru.sushchenko.trelloclone.service.CommentService;
import ru.sushchenko.trelloclone.service.TaskService;

import java.util.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task related actions")
public class TaskController {
    private final TaskService taskService;
    private final ChecklistService checklistService;
    private final AttachmentService attachmentService;
    private final CommentService commentService;

    @Operation(summary = "Get all tasks")
    @SecurityRequirement(name = "JWT")
    @GetMapping("")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Operation(summary = "Get all tasks with dynamic filter")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/filters")
    public ResponseEntity<List<TaskResponse>> getAllTasksWithFilters(@Valid @RequestBody(required = false) TaskFilterRequest taskFilter) {
        return ResponseEntity.ok(taskService.getAllTasksWithFilters(taskFilter));
    }

    @Operation(summary = "Get task by id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "Add task")
    @SecurityRequirement(name = "JWT")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskResponse> addTask(@Valid @RequestBody TaskRequest taskDto,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(taskService.addTask(taskDto, userPrincipal.getUser()), HttpStatus.CREATED);
    }

    @Operation(summary = "Update task by id if user is creator or executor")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskResponse> updateTaskById(@PathVariable UUID id,
                                                       @Valid @RequestBody TaskRequest taskDto,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(taskService.updateTaskById(id, taskDto, userPrincipal.getUser()));
    }

    @Operation(summary = "Delete task by id if user is creator")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteTaskById(@PathVariable UUID id,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        taskService.deleteTaskById(id, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Update task status by id if user is creator or executor")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskResponse> updateTaskStatusById(@PathVariable UUID id,
                                                             @Valid @RequestBody TaskStatusRequest taskStatusRequest,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(taskService.updateTaskStatusById(id, taskStatusRequest, userPrincipal.getUser()));
    }

    @Operation(summary = "Add comment to task by id")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponse> addCommentToTaskById(@PathVariable UUID id,
                                                                @Valid @RequestBody CommentRequest commentDto,
                                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(commentService.addCommentToTaskById(id, commentDto, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Get comments by task id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByTaskId(@PathVariable UUID id) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(id));
    }

    @Operation(summary = "Add attachments to task by id")
    @SecurityRequirement(name = "JWT")
    @RequestMapping(value = "/{id}/attachments",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<AttachmentResponse>> addAttachmentsToTaskById(@PathVariable UUID id,
                                                                     @Valid @RequestPart List<MultipartFile> attachments,
                                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(attachmentService.addAttachmentsToTask(id, attachments, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Get attachments by task id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachmentsByTaskId(@PathVariable UUID id) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByTaskId(id));
    }

    @Operation(summary = "Add executor to task by id")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/executors/{executorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskResponse> addExecutorToTaskById(@PathVariable UUID id,
                                                              @PathVariable UUID executorId,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(taskService.addExecutorToTaskById(id, executorId, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Delete executor from task by id")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}/executors/{executorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> removeExecutorFromTaskById(@PathVariable UUID id,
                                                      @PathVariable UUID executorId,
                                                      @AuthenticationPrincipal UserPrincipal userPrincipal) {
        taskService.removeExecutorFromTaskById(id, executorId, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete attachment from task by id")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}/attachments/{attachmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> removeAttachmentFromTaskById(@PathVariable UUID id,
                                                        @PathVariable UUID attachmentId,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        attachmentService.removeAttachmentFromTaskById(id, attachmentId, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Add checklist to task by id")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/checklists")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ChecklistResponse> addChecklistToTaskById(@PathVariable UUID id,
                                                                    @Valid @RequestBody ChecklistRequest checklistDto,
                                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(checklistService.addChecklistToTask(id, checklistDto, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Get checklists by task id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/checklists")
    public ResponseEntity<List<ChecklistResponse>> getChecklistsByTaskId(@PathVariable UUID id) {
        return ResponseEntity.ok(checklistService.getChecklistsByTaskId(id));
    }
}
