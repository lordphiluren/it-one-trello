package ru.sushchenko.trelloclone.dto.checklist;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.sushchenko.trelloclone.dto.checklist.checkitem.CheckItemRequest;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChecklistRequest {
    @NotNull(message = "Checklist name can't be empty")
    private String name;
    @NotEmpty(message = "Checkitems can't be empty")
    private Set<CheckItemRequest> checkItems;
}
