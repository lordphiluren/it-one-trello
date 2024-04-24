package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.AuthRequest;
import ru.sushchenko.trelloclone.dto.AuthResponse;

public interface AuthService {
    AuthResponse attemptLogin(String username, String password);
    void signUp(AuthRequest authRequest);
}
