package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.checklist.ChecklistRequest;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.entity.Task;

import java.util.List;
import java.util.UUID;

public interface ChecklistService {
    ChecklistResponse addChecklist(ChecklistRequest checklistDto, Task task);
    List<ChecklistResponse> getChecklistsByTaskId(UUID taskId);
    void deleteChecklistById(UUID id);
    ChecklistResponse updateChecklistById(UUID checklistId, ChecklistRequest checklistDto);
}
