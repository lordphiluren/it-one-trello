package ru.sushchenko.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sushchenko.trelloclone.dto.comment.CommentRequest;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.entity.Comment;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    public CommentResponse toDto(Comment comment) {
        UserResponse userDto = userMapper.toDto(comment.getCreator());
        Set<String> attachmentsDto = comment.getAttachments().stream()
                .map(attach -> attach.getId().getUrl())
                .collect(Collectors.toSet());
        CommentResponse commentDto = modelMapper.map(comment, CommentResponse.class);
        commentDto.setAttachments(attachmentsDto);
        commentDto.setCreator(userDto);
        return commentDto;
    }
    public Comment toEntity(CommentRequest commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }
    public void mergeDtoIntoEntity(CommentRequest commentDto, Comment comment) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(commentDto, comment);
    }
}
