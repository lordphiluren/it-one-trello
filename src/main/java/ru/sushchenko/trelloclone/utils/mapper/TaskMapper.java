package ru.sushchenko.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.dto.user.UserRequest;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    public TaskResponse toDto(Task task) {
        UserResponse creatorDto = userMapper.toDto(task.getCreator());
        Set<UserResponse> executorsDto = task.getExecutors().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toSet());
        Set<CommentResponse> commentsDto = task.getComments().stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toSet());
        Set<String> attachmentsDto = task.getAttachments().stream()
                .map(a -> a.getId().getUrl())
                .collect(Collectors.toSet());
        Set<String> tagsDto = task.getTags().stream()
                .map(t -> t.getId().getTag())
                .collect(Collectors.toSet());

        TaskResponse taskDto = modelMapper.map(task, TaskResponse.class);
        taskDto.setCreator(creatorDto);
        taskDto.setExecutors(executorsDto);
        taskDto.setComments(commentsDto);
        taskDto.setAttachments(attachmentsDto);
        taskDto.setTags(tagsDto);

        return taskDto;
    }
    public Task toEntity(TaskRequest taskDto) {
        return modelMapper.map(taskDto, Task.class);
    }
    public void mergeDtoIntoEntity(TaskRequest taskDto, Task task) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(taskDto, task);
    }
}
