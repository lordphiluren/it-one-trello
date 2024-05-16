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
import ru.sushchenko.trelloclone.dto.user.UserRequest;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.security.UserPrincipal;
import ru.sushchenko.trelloclone.service.UserService;
import ru.sushchenko.trelloclone.utils.validation.UpdateValidation;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User account actions")
public class UserController {
    private final UserService userService;

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

    @Operation(summary = "Partial update of user info by id")
    @SecurityRequirement(name = "JWT")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateUserInfo(@PathVariable UUID id,
                                            @Validated(UpdateValidation.class) @RequestBody UserRequest userDto,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userService.updateUserById(id, userDto, userPrincipal.getUser()));
    }
}
