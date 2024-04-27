package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.auth.AuthRequest;
import ru.sushchenko.trelloclone.dto.auth.AuthResponse;

public interface AuthService {
    AuthResponse attemptLogin(String username, String password);
    void signUp(AuthRequest authRequest);
}
