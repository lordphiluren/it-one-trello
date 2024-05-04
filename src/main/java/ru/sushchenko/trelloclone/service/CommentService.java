package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.comment.CommentRequest;
import ru.sushchenko.trelloclone.dto.comment.CommentResponse;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;

public interface CommentService {
    CommentResponse addComment(CommentRequest commentDto, Task task, User creator);
}
