package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.checklist.ChecklistRequest;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.entity.Task;

public interface ChecklistService {
    ChecklistResponse addChecklist(ChecklistRequest checklistDto, Task task);
}
