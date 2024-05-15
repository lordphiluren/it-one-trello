package ru.sushchenko.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistRequest;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.entity.Checklist;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.repo.ChecklistRepo;
import ru.sushchenko.trelloclone.service.ChecklistService;
import ru.sushchenko.trelloclone.service.TaskService;
import ru.sushchenko.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.sushchenko.trelloclone.utils.exception.ResourceNotFoundException;
import ru.sushchenko.trelloclone.utils.mapper.ChecklistMapper;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {
    private final ChecklistRepo checklistRepo;
    private final ChecklistMapper checklistMapper;
    private final TaskService taskService;

    @Override
    @Transactional
    public ChecklistResponse addChecklistToTask(UUID taskId, ChecklistRequest checklistDto, User currentUser) {
        Task task = taskService.getExistingTask(taskId);
        if(taskService.checkIfAllowedToModifyTask(task, currentUser)) {
            Checklist checklist = checklistMapper.toEntity(checklistDto);
            checklist.setCheckItems(new HashSet<>());
            checklist.setTask(task);
            Checklist savedChecklist = checklistRepo.save(checklist);
            log.info("Checklist with id: {} added for task with id: {}", savedChecklist.getId(), taskId);
            return checklistMapper.toDto(savedChecklist);
        } else {
            throw new NotEnoughPermissionsException(currentUser.getId(), taskId);
        }
    }

    @Override
    public List<ChecklistResponse> getChecklistsByTaskId(UUID taskId) {
        return checklistRepo.findByTaskId(taskId).stream()
                .map(checklistMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteChecklistById(UUID id, User currentUser) {
        Checklist checklist = getExistingChecklist(id);
        Task task = checklist.getTask();
        if(taskService.checkIfAllowedToModifyTask(task, currentUser)) {
            checklistRepo.deleteById(id);
            log.info("Checklist with id: {} deleted by user with id: {}", id, currentUser.getId());
        } else {
            throw new NotEnoughPermissionsException(currentUser.getId(), id);
        }
    }

    @Override
    public ChecklistResponse updateChecklistById(UUID id, ChecklistRequest checklistDto, User currentUser) {
        Checklist checklist = getExistingChecklist(id);
        Task task = checklist.getTask();
        if(taskService.checkIfAllowedToModifyTask(task, currentUser)) {
            checklistMapper.mergeEntityToDto(checklistDto, checklist);
            Checklist updatedChecklist = checklistRepo.save(checklist);
            log.info("Checklist with id: {} updated by user with id: {}", id, currentUser.getId());
            return checklistMapper.toDto(updatedChecklist);
        } else {
            throw new NotEnoughPermissionsException(currentUser.getId(), id);
        }
    }

    private Checklist getExistingChecklist(UUID id) {
        return checklistRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }
}
