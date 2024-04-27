package ru.sushchenko.trelloclone.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.sushchenko.trelloclone.dto.ResponseMessage;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.security.UserPrincipal;
import ru.sushchenko.trelloclone.service.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task related actions")
public class TaskController {
    private final TaskService taskService;
    @Operation(summary = "Get all tasks with dynamic filter")
    @SecurityRequirement(name = "JWT")
    @GetMapping("")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
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
}
