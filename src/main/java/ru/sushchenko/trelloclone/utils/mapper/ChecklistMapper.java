package ru.sushchenko.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistRequest;
import ru.sushchenko.trelloclone.dto.checklist.ChecklistResponse;
import ru.sushchenko.trelloclone.dto.checklist.checkitem.CheckItemResponse;
import ru.sushchenko.trelloclone.entity.CheckItem;
import ru.sushchenko.trelloclone.entity.Checklist;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChecklistMapper {
    private final ModelMapper modelMapper;
    private final CheckItemMapper checkItemMapper;

    public Checklist toEntity(ChecklistRequest checklistDto) {
        Checklist checklist =  modelMapper.map(checklistDto, Checklist.class);
        Set<CheckItem> checkItems = checklistDto.getCheckItems().stream().map(checkItemDto -> {
            CheckItem checkItem = checkItemMapper.toEntity(checkItemDto);
            checkItem.setChecklist(checklist);
            return checkItem;
        }).collect(Collectors.toSet());
        checklist.setCheckItems(checkItems);
        return checklist;
    }

    public ChecklistResponse toDto(Checklist checklist) {
        ChecklistResponse checklistDto = modelMapper.map(checklist, ChecklistResponse.class);
        Set<CheckItemResponse> checkItemsDto = checklist.getCheckItems().stream()
                .map(checkItemMapper::toDto)
                .collect(Collectors.toSet());
        checklistDto.setCheckItems(checkItemsDto);

        return checklistDto;
    }
    public void mergeEntityToDto(ChecklistRequest checklistDto, Checklist checklist) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(checklistDto, checklist);
    }
}
