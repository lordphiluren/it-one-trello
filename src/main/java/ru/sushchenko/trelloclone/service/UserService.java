package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.UserRequest;
import ru.sushchenko.trelloclone.dto.UserResponse;
import ru.sushchenko.trelloclone.entity.User;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse updateUserById(Long id, UserRequest userDto, User currentUser);
}
