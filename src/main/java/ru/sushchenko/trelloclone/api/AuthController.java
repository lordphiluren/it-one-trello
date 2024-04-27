package ru.sushchenko.trelloclone.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.sushchenko.trelloclone.dto.auth.AuthRequest;
import ru.sushchenko.trelloclone.dto.auth.AuthResponse;
import ru.sushchenko.trelloclone.service.AuthService;
import ru.sushchenko.trelloclone.utils.validation.LoginRequest;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authorization", description = "Controller for user authorization and authentication using JWT")
public class AuthController {
    private final AuthService authService;
    @Operation(
            summary = "User login",
            description = "Handles user authentication"
    )
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthResponse> login(@Validated({LoginRequest.class}) @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.attemptLogin(authRequest.getUsername(), authRequest.getPassword()));
    }
    @Operation(
            summary = "User signup",
            description = "Handles user registration"
    )
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> signUp(@Valid @RequestBody AuthRequest authRequest) {
        authService.signUp(authRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
