package ru.sushchenko.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sushchenko.trelloclone.dto.kafka.AuthNotificationDto;
import ru.sushchenko.trelloclone.dto.kafka.HotTaskDto;
import ru.sushchenko.trelloclone.dto.kafka.UserHotTaskDto;
import ru.sushchenko.trelloclone.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KafkaMapper {
    private final ModelMapper modelMapper;

    public AuthNotificationDto toAuthNotificationDto(User user) {
        return modelMapper.map(user, AuthNotificationDto.class);
    }

    public UserHotTaskDto toHotTaskDto(User user) {
        UserHotTaskDto userHotTaskDto = modelMapper.map(user, UserHotTaskDto.class);

        List<HotTaskDto> hotTaskDtos = user.getAssignedTasks().stream()
                .map(at -> {
                    HotTaskDto hotTaskDto =  modelMapper.map(at, HotTaskDto.class);
                    hotTaskDto.setPriority(at.getPriority().getValue());
                    hotTaskDto.setStatus(at.getStatus().getValue());
                    return hotTaskDto;
                })
                .collect(Collectors.toList());

        userHotTaskDto.setHotTasks(hotTaskDtos);

        return userHotTaskDto;
    }
}
