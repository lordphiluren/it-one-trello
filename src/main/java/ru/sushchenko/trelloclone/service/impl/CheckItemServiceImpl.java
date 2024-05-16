package ru.sushchenko.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sushchenko.trelloclone.dto.checkitem.CheckItemRequest;
import ru.sushchenko.trelloclone.dto.checkitem.CheckItemResponse;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.entity.CheckItem;
import ru.sushchenko.trelloclone.entity.Checklist;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.repo.CheckItemRepo;
import ru.sushchenko.trelloclone.service.CheckItemService;
import ru.sushchenko.trelloclone.service.ChecklistService;
import ru.sushchenko.trelloclone.service.TaskService;
import ru.sushchenko.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.sushchenko.trelloclone.utils.exception.ResourceMismatchException;
import ru.sushchenko.trelloclone.utils.exception.ResourceNotFoundException;
import ru.sushchenko.trelloclone.utils.mapper.CheckItemMapper;
import ru.sushchenko.trelloclone.utils.mapper.ChecklistMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckItemServiceImpl implements CheckItemService {
    private final CheckItemRepo checkItemRepo;
    private final ChecklistService checklistService;
    private final TaskService taskService;
    private final ChecklistMapper checklistMapper;
    private final CheckItemMapper checkItemMapper;

    @Override
    @Transactional
    public ChecklistResponse addCheckItemsToChecklistById(UUID checklistId, Set<CheckItemRequest> checkItemsDto,
                                                          User currentUser) {
        Checklist checklist = checklistService.getExistingChecklist(checklistId);
        Task task = checklist.getTask();

        taskService.validatePermissions(task, currentUser);

        Set<CheckItem> checkItemsToAdd = checkItemsDto.stream()
                .map(checkItemMapper::toEntity)
                .peek(c -> c.setChecklist(checklist))
                .collect(Collectors.toSet());

        List<CheckItem> savedCheckItems = checkItemRepo.saveAll(checkItemsToAdd);

        Set<CheckItem> checkItems = new HashSet<>(checklist.getCheckItems());
        checkItems.addAll(savedCheckItems);
        checklist.setCheckItems(checkItems);

        log.info("{} check items added for checklist with id: {}", savedCheckItems.size(), checklist.getId());
        return checklistMapper.toDto(checklist);

    }

    @Override
    @Transactional
    public CheckItemResponse updateCheckItemById(UUID checklistId, UUID checkItemId,
                                                 CheckItemRequest checkItemDto, User currentUser) {
        CheckItem checkItemToUpdate = getExistingCheckItem(checkItemId);
        validateChecklistOwnership(checklistId, checkItemToUpdate);

        Task task = checkItemToUpdate.getChecklist().getTask();
        taskService.validatePermissions(task, currentUser);

        checkItemMapper.mergeDtoIntoEntity(checkItemDto, checkItemToUpdate);
        CheckItem updatedCheckItem = checkItemRepo.save(checkItemToUpdate);

        log.info("Checkitem with id: {} updated by user with id: {}", checkItemId, currentUser.getId());
        return checkItemMapper.toDto(updatedCheckItem);
    }

    @Override
    @Transactional
    public void deleteCheckItemById(UUID checklistId, UUID checkItemId, User currentUser) {
        CheckItem checkItemToDelete = getExistingCheckItem(checkItemId);
        validateChecklistOwnership(checklistId, checkItemToDelete);

        Task task = checkItemToDelete.getChecklist().getTask();
        taskService.validatePermissions(task, currentUser);

        checkItemRepo.deleteById(checkItemId);
        log.info("Checkitem with id: {} deleted by user with id: {}", checkItemId, currentUser.getId());
    }

    private CheckItem getExistingCheckItem(UUID id) {
        return checkItemRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    private void validateChecklistOwnership(UUID checklistId, CheckItem checkItem) {
        if (!checkItem.getChecklist().getId().equals(checklistId)) {
            throw new ResourceMismatchException("Checkitem with id: " + checkItem.getId() +
                    " doesn't belong to checklist with id: " + checklistId);
        }
    }
}
