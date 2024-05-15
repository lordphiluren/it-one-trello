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
import ru.sushchenko.trelloclone.service.TaskService;
import ru.sushchenko.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.sushchenko.trelloclone.utils.exception.ResourceNotFoundException;
import ru.sushchenko.trelloclone.utils.mapper.CommentMapper;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepo commentRepo;
    private final TaskService taskService;
    @Override
    @Transactional
    public CommentResponse addCommentToTaskById(UUID taskId, CommentRequest commentDto, User currentUser) {
        Task task = taskService.getExistingTask(taskId);
        if(taskService.checkIfAllowedToModifyTask(task, currentUser)) {
            Comment comment = commentMapper.toEntity(commentDto);
            comment.setTask(task);
            comment.setCreator(currentUser);
            enrichComment(comment);
            Comment savedComment = commentRepo.save(comment);
            log.info("Comment with id: {} created", savedComment.getId());
            return commentMapper.toDto(savedComment);
        } else {
            throw new NotEnoughPermissionsException(currentUser.getId(), taskId);
        }
    }

    @Override
    public List<CommentResponse> getCommentsByTaskId(UUID taskId) {
        return commentRepo.findByTaskId(taskId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse updateCommentById(UUID id, CommentRequest commentDto, User currentUser) {
        Comment comment = getExistingComment(id);
        if(checkIfCreator(comment, currentUser)) {
            commentMapper.mergeDtoIntoEntity(commentDto, comment);
            Comment savedComment = commentRepo.save(comment);
            log.info("Comment with id: {} was edited", savedComment.getId());
            return commentMapper.toDto(savedComment);
        } else {
            throw new NotEnoughPermissionsException(currentUser.getId(), id);
        }
    }

    @Override
    @Transactional
    public void deleteCommentById(UUID id, User currentUser) {
        Comment comment = getExistingComment(id);
        if(checkIfCreator(comment, currentUser)) {
            commentRepo.deleteById(id);
        } else {
            throw new NotEnoughPermissionsException(currentUser.getId(), id);
        }
    }

    private Comment getExistingComment(UUID id) {
        return commentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }
    private void enrichComment(Comment comment) {
        comment.setCreatedAt(new Date());
    }
    private boolean checkIfCreator(Comment comment, User currentUser) {
        return Objects.equals(comment.getCreator().getId(), currentUser.getId());
    }
}
