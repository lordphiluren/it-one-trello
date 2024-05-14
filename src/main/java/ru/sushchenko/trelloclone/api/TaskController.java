package ru.sushchenko.trelloclone.api;

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
import ru.sushchenko.trelloclone.dto.ResponseMessage;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.entity.Checklist;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.security.UserPrincipal;
import ru.sushchenko.trelloclone.service.TaskService;

import java.util.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task related actions")
public class TaskController {
    private final TaskService taskService;
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
    public ResponseEntity<ResponseMessage> deleteTaskById(@PathVariable UUID id,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        taskService.deleteTaskById(id, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @Operation(summary = "Add comment to task by id")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponse> addCommentToTaskById(@PathVariable UUID id,
                                                                @Valid @RequestBody CommentRequest commentDto,
                                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(taskService.addCommentToTaskById(id, commentDto, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }
    @Operation(summary = "Get comments by task id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByTaskId(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getCommentsByTaskId(id));
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
        return new ResponseEntity<>(taskService.addAttachmentsToTaskById(id, attachments, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }
    @Operation(summary = "Get attachments by task id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachmentsByTaskId(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getAttachmentsByTaskId(id));
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
    public ResponseEntity<ResponseMessage> removeExecutorFromTaskById(@PathVariable UUID id,
                                                              @PathVariable UUID executorId,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        taskService.removeExecutorFromTaskById(id, executorId, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @Operation(summary = "Delete attachment from task by id")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}/attachments/{attachmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ResponseMessage> removeAttachmentFromTaskById(@PathVariable UUID id,
                                                                @PathVariable UUID attachmentId,
                                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        taskService.removeAttachmentFromTaskById(id, attachmentId, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @Operation(summary = "Add checklist to task by id")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/checklists")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ChecklistResponse> addChecklistToTaskById(@PathVariable UUID id,
                                                                    @Valid @RequestBody ChecklistRequest checklistDto,
                                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(taskService.addChecklistToTaskById(id, checklistDto, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }
    @Operation(summary = "Get checklists by task id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/checklists")
    public ResponseEntity<List<ChecklistResponse>> getChecklistsByTaskId(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getChecklistsByTaskId(id));
    }
    @Operation(summary = "Delete checklist from task by id")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}/checklists/{checklistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ResponseMessage> deleteChecklistById(@PathVariable UUID id,
                                                               @PathVariable UUID checklistId,
                                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        taskService.deleteChecklistById(id, checklistId, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @Operation(summary = "Update checklist on task by id")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{id}/checklists/{checklistId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ChecklistResponse> updateChecklistById(@PathVariable UUID id,
                                                               @PathVariable UUID checklistId,
                                                               @Valid @RequestBody ChecklistRequest checklistDto,
                                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(taskService.updateChecklistById(id, checklistId, checklistDto, userPrincipal.getUser()));
    }
}
