package ru.sushchenko.trelloclone.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    @Operation(summary = "Get all tasks with dynamic filter")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/filters")
    public ResponseEntity<List<TaskResponse>> getAllTasks(@RequestBody(required = false) TaskFilterRequest taskFilter) {
        return ResponseEntity.ok(taskService.getAllTasks(taskFilter));
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
    public ResponseEntity<TaskResponse> addTask(@RequestBody TaskRequest taskDto,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(taskService.addTask(taskDto, userPrincipal.getUser()), HttpStatus.CREATED);
    }
    @Operation(summary = "Partially update task by id if user is creator or executor")
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskResponse> updateTaskById(@PathVariable UUID id,
                                                       @RequestBody TaskRequest taskDto,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(taskService.updateTaskById(id, taskDto, userPrincipal.getUser()));
    }
    @Operation(summary = "Delete task by id if user is creator")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseMessage> deleteTaskById(@PathVariable UUID id,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        taskService.deleteTaskById(id, userPrincipal.getUser());
        return ResponseEntity.ok(ResponseMessage.builder().message("Task successfully deleted").build());
    }
    @Operation(summary = "Add comment to task by id")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponse> addCommentToTaskById(@PathVariable UUID id,
                                                                @RequestBody CommentRequest commentDto,
                                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(taskService.addCommentToTaskById(id, commentDto, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }
}
