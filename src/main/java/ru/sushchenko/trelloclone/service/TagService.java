package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.entity.Tag;
import ru.sushchenko.trelloclone.entity.Task;

import java.util.Set;

public interface TagService {
    void deleteTagsByTask(Task task);
}
