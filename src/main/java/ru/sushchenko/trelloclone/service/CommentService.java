package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.comment.CommentRequest;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentResponse addComment(CommentRequest commentDto, Task task, User creator);
    List<CommentResponse> getCommentsByTaskId(UUID taskId);
}
