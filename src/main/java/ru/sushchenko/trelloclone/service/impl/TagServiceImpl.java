package ru.sushchenko.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sushchenko.trelloclone.entity.Tag;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.repo.TagRepo;
import ru.sushchenko.trelloclone.service.TagService;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepo tagRepo;

    @Override
    @Transactional
    public void deleteTagsByTask(Task task) {
        tagRepo.deleteAllByTask(task);
    }
}
