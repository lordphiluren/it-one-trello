package ru.sushchenko.trelloclone.service;

import org.springframework.transaction.annotation.Transactional;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistRequest;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChecklistService {
    ChecklistResponse addChecklistToTask(UUID taskId, ChecklistRequest checklistDto, User currentUser);
    List<ChecklistResponse> getChecklistsByTaskId(UUID taskId);
    void deleteChecklistById(UUID id, User currentUser);
    ChecklistResponse updateChecklistById(UUID checklistId, ChecklistRequest checklistDto, User currentUser);
}
