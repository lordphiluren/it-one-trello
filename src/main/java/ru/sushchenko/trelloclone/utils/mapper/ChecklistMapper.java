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
        return modelMapper.map(checklistDto, Checklist.class);
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
