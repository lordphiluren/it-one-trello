package ru.sushchenko.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistRequest;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.entity.Checklist;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.repo.ChecklistRepo;
import ru.sushchenko.trelloclone.service.ChecklistService;
import ru.sushchenko.trelloclone.utils.mapper.ChecklistMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {
    private final ChecklistRepo checklistRepo;
    private final ChecklistMapper checklistMapper;

    @Override
    @Transactional
    public ChecklistResponse addChecklist(ChecklistRequest checklistDto, Task task) {
        Checklist checklist = checklistMapper.toEntity(checklistDto);
        checklist.setTask(task);
        return checklistMapper.toDto(checklistRepo.save(checklist));
    }

    @Override
    public List<ChecklistResponse> getChecklistsByTaskId(UUID taskId) {
        return checklistRepo.findByTaskId(taskId).stream()
                .map(checklistMapper::toDto)
                .collect(Collectors.toList());
    }
}
