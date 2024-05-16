package ru.sushchenko.trelloclone.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.sushchenko.trelloclone.dto.checkitem.CheckItemRequest;
import ru.sushchenko.trelloclone.dto.checkitem.CheckItemResponse;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistRequest;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.security.UserPrincipal;
import ru.sushchenko.trelloclone.service.CheckItemService;
import ru.sushchenko.trelloclone.service.ChecklistService;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checklists")
@RequiredArgsConstructor
@Tag(name = "Checklists", description = "Checklist related actions")
public class ChecklistController {
    private final ChecklistService checklistService;
    private final CheckItemService checkItemService;

    @Operation(summary = "Delete checklist from task by id")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteChecklistById(@PathVariable UUID id,
                                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        checklistService.deleteChecklistById(id, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @Operation(summary = "Update checklist on task by id")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ChecklistResponse> updateChecklistById(@PathVariable UUID id,
                                                                 @Valid @RequestBody ChecklistRequest checklistDto,
                                                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(checklistService.updateChecklistById(id, checklistDto, userPrincipal.getUser()));
    }
    @Operation(summary = "Add check items to checklist")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/checkitems")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ChecklistResponse> addCheckItemsToChecklist(@PathVariable UUID id,
                                                                      @Valid @RequestBody Set<CheckItemRequest> checkItemsDto,
                                                                      @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(
                checkItemService.addCheckItemsToChecklistById(id, checkItemsDto, userPrincipal.getUser())
        );
    }
    @Operation(summary = "Update check item on checklist")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{id}/checkitems/{checkItemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CheckItemResponse> updateCheckItemOnChecklist(@PathVariable UUID id,
                                                                        @PathVariable UUID checkItemId,
                                                                      @Valid @RequestBody CheckItemRequest checkItemDto,
                                                                      @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(
                checkItemService.updateCheckItemById(id, checkItemId, checkItemDto, userPrincipal.getUser())
        );
    }
    @Operation(summary = "Delete check item from checklist")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}/checkitems/{checkItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteCheckItemFromChecklist(@PathVariable UUID id,
                                                          @PathVariable UUID checkItemId,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        checkItemService.deleteCheckItemById(id, checkItemId, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
