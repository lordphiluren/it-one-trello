package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.user.UserRequest;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.entity.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(UUID id);
    UserResponse updateUserById(UUID id, UserRequest userDto, User currentUser);
    Set<User> getUsersByIdIn(Set<UUID> ids);
}
