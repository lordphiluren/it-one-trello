package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.auth.AuthRequest;
import ru.sushchenko.trelloclone.dto.auth.AuthResponse;
import ru.sushchenko.trelloclone.dto.auth.RegistrationRequest;

public interface AuthService {
    AuthResponse attemptLogin(String username, String password);
    void signUp(RegistrationRequest authRequest);
}
