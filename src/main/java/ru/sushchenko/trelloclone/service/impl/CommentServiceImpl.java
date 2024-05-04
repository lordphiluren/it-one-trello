package ru.sushchenko.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sushchenko.trelloclone.dto.comment.CommentRequest;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.entity.Comment;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.repo.CommentRepo;
import ru.sushchenko.trelloclone.service.CommentService;
import ru.sushchenko.trelloclone.utils.mapper.CommentMapper;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepo commentRepo;
    @Override
    @Transactional
    public CommentResponse addComment(CommentRequest commentDto, Task task, User creator) {
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setTask(task);
        comment.setCreator(creator);
        enrichComment(comment);
        Comment savedComment = commentRepo.save(comment);
        log.info("Comment with id: {} created", savedComment.getId());
        return commentMapper.toDto(savedComment);
    }

    @Override
    public List<CommentResponse> getCommentsByTaskId(UUID taskId) {
        return commentRepo.findByTaskId(taskId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    private void enrichComment(Comment comment) {
        comment.setCreatedAt(new Date());
    }
}
