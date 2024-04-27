package ru.sushchenko.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sushchenko.trelloclone.dto.auth.AuthRequest;
import ru.sushchenko.trelloclone.dto.user.UserRequest;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.entity.User;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;
    public User toEntity(UserRequest userDto) {
        return modelMapper.map(userDto, User.class);
    }
    public User toEntity(AuthRequest authDto) {
        return modelMapper.map(authDto, User.class);
    }

    public UserResponse toDto(User user) {
        return modelMapper.map(user, UserResponse.class);
    }
    public void mergeDtoIntoEntity(UserRequest userDto, User user) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(userDto, user);
    }
}
