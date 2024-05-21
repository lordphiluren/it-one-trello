package ru.sushchenko.trelloclone.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.sushchenko.trelloclone.dto.board.AddBoardRequest;
import ru.sushchenko.trelloclone.dto.board.BoardRequest;
import ru.sushchenko.trelloclone.dto.board.BoardResponse;
import ru.sushchenko.trelloclone.dto.task.TaskFilterRequest;
import ru.sushchenko.trelloclone.dto.task.TaskFilterSort;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.security.UserPrincipal;
import ru.sushchenko.trelloclone.service.BoardService;
import ru.sushchenko.trelloclone.service.TaskService;
import ru.sushchenko.trelloclone.service.UserService;
import ru.sushchenko.trelloclone.utils.validation.UpdateValidation;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
@Tag(name = "Boards", description = "Boards related actions")
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;
    private final TaskService taskService;

    @Operation(summary = "Get all boards")
    @SecurityRequirement(name = "JWT")
    @GetMapping("")
    public ResponseEntity<List<BoardResponse>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    @Operation(summary = "Get Board by id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable UUID id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @Operation(summary = "Add board")
    @SecurityRequirement(name = "JWT")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BoardResponse> addBoard(@Valid @RequestBody AddBoardRequest boardDto,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(boardService.addBoard(boardDto, userPrincipal.getUser()), HttpStatus.CREATED);
    }

    @Operation(summary = "Update board by id if user is creator")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BoardResponse> updateBoardById(@PathVariable UUID id,
                                                        @Validated({UpdateValidation.class}) @RequestBody BoardRequest boardDto,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(boardService.updateBoardById(id, boardDto, userPrincipal.getUser()));
    }

    @Operation(summary = "Delete board by id if user is creator")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteBoardById(@PathVariable UUID id,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        boardService.deleteBoardById(id, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Add task to board by id")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskResponse> addTaskToBoardById(@PathVariable UUID id,
                                                                @Valid @RequestBody TaskRequest taskDto,
                                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(taskService.addTaskToBoardById(id, taskDto, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Get tasks on board by id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByBoardId(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTasksByBoardId(id));
    }

    @Operation(summary = "Get tasks by board by id with filter")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/tasks/filters")
    public ResponseEntity<List<TaskResponse>> getTasksByBoardIdWithFilter(@PathVariable UUID id,
                                                                          @Valid @RequestBody TaskFilterRequest taskFilterRequest) {
        return ResponseEntity.ok(taskService.getTasksByBoardIdWithFilters(id, taskFilterRequest));
    }

    @Operation(summary = "Get board members")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<UserResponse>> getBoardMembersById(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.getUsersByBoardId(id),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Add member to board by id")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/members/{memberId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BoardResponse> addMemberToBoardById(@PathVariable UUID id,
                                                                    @PathVariable UUID memberId,
                                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(boardService.addMemberToBoardById(id, memberId, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Delete member from board by id")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> removeMemberFromBoardById(@PathVariable UUID id,
                                                        @PathVariable UUID memberId,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        boardService.removeMemberFromBoardById(id, memberId, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
