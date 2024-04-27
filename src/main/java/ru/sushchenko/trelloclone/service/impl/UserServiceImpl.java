package ru.sushchenko.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sushchenko.trelloclone.dto.user.UserRequest;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.repo.UserRepo;
import ru.sushchenko.trelloclone.service.UserService;
import ru.sushchenko.trelloclone.utils.exception.EntityAlreadyExistException;
import ru.sushchenko.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.sushchenko.trelloclone.utils.exception.UserNotFoundException;
import ru.sushchenko.trelloclone.utils.mapper.UserMapper;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    @Override
    @Transactional
    public UserResponse getUserById(UUID id) {
        return userMapper.toDto(getExistingUser(id));
    }
    @Override
    @Transactional
    public UserResponse updateUserById(UUID id, UserRequest userDto, User currentUser) {
        User user = getExistingUser(id);
        if(checkIfAllowedToModifyUser(user, currentUser)) {
            try {
                userMapper.mergeDtoIntoEntity(userDto, user);
                User savedUser = userRepo.saveAndFlush(user);
                log.info("User with id: {} updated", savedUser.getId());
                return userMapper.toDto(savedUser);
            }
            catch (DataIntegrityViolationException e) {
                throw new EntityAlreadyExistException("User with this username or email already exists");
            }
        } else {
            log.warn("User with id: {} tried to modify user with id: {}", currentUser.getId(), user.getId());
            throw new NotEnoughPermissionsException("User with id: " + currentUser.getId() +
                    " can't modify user with id: " + id);
        }
    }

    @Override
    @Transactional
    public Set<User> getUsersByIdIn(Set<UUID> ids) {
        Set<User> users = userRepo.findByIdIn(ids);
        if(users.isEmpty()) {
            throw new UserNotFoundException(ids);
        } else {
            return users;
        }
    }

    private User getExistingUser(UUID id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
    private boolean checkIfAllowedToModifyUser(User user, User currentUser) {
        return Objects.equals(user.getId(), currentUser.getId());
    }
}
