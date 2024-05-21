package ru.sushchenko.trelloclone.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.sushchenko.trelloclone.dto.board.BoardResponse;
import ru.sushchenko.trelloclone.dto.kafka.UserHotTaskDto;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.dto.user.UserRequest;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.security.UserPrincipal;
import ru.sushchenko.trelloclone.service.BoardService;
import ru.sushchenko.trelloclone.service.TaskService;
import ru.sushchenko.trelloclone.service.UserService;
import ru.sushchenko.trelloclone.utils.validation.UpdateValidation;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User account actions")
public class UserController {
    private final UserService userService;
    private final TaskService taskService;
    private final BoardService boardService;

    @Operation(summary = "Get all users")
    @SecurityRequirement(name = "JWT")
    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Get user by id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Get tasks assigned to user")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/assigned-tasks")
    public ResponseEntity<List<TaskResponse>> getAssignedTasksByUserId(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getAssignedTasksByUserId(id));
    }

    @Operation(summary = "Get tasks created by user")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/created-tasks")
    public ResponseEntity<List<TaskResponse>> getCreatedTasksByUserId(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getCreatedTasksByUserId(id));
    }

    @Operation(summary = "Get boards in which user is a member")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/membered-boards")
    public ResponseEntity<List<BoardResponse>> getMemberedBoardsByUserId(@PathVariable UUID id) {
        return ResponseEntity.ok(boardService.getMemberedBoardsByUserId(id));
    }

    @Operation(summary = "Get boards created by user")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/created-boards")
    public ResponseEntity<List<BoardResponse>> getCreatedBoardsByUserId(@PathVariable UUID id) {
        return ResponseEntity.ok(boardService.getCreatedBoardsByUserId(id));
    }

    @Operation(summary = "Update of user info by id")
    @SecurityRequirement(name = "JWT")
    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateUserInfo(@PathVariable UUID id,
                                            @Validated(UpdateValidation.class) @RequestBody UserRequest userDto,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userService.updateUserById(id, userDto, userPrincipal.getUser()));
    }
}
