package ru.sushchenko.trelloclone.dto.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.sushchenko.trelloclone.entity.enums.Priority;
import ru.sushchenko.trelloclone.entity.enums.Status;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskRequest {
    @NotBlank(message = "Task name can't be null")
    private String name;
    @Size(max = 1024, message = "Task description length can't be more than 1024")
    private String description;
    @NotNull(message = "Priority can't be null")
    private Priority priority;
    @NotNull(message = "Status can't be null")
    private Status status;
    @NotEmpty(message = "Task should have at least 1 executor")
    private Set<UUID> executorIds;
    private Set<String> tags;
    @NotNull(message = "End date can't be null")
    private Date endDate;
}
