package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.comment.CommentRequest;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentResponse addCommentToTaskById(UUID taskId,CommentRequest commentDto,User currentUser);
    List<CommentResponse> getCommentsByTaskId(UUID taskId);
    CommentResponse updateCommentById(UUID id, CommentRequest commentDto, User currentUser);
    void deleteCommentById(UUID id, User currentUser);
}
