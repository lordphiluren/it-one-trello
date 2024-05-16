package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.checkitem.CheckItemRequest;
import ru.sushchenko.trelloclone.dto.checkitem.CheckItemResponse;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.entity.User;

import java.util.Set;
import java.util.UUID;

public interface CheckItemService {
    ChecklistResponse addCheckItemsToChecklistById(UUID checklistId, Set<CheckItemRequest> checkItemsDto,
                                                   User currentUser);
    CheckItemResponse updateCheckItemById(UUID checklistId, UUID checkItemId,
                                          CheckItemRequest checkItemDto, User currentUser);
    void deleteCheckItemById(UUID checklistId, UUID checkItemId, User currentUser);
}
