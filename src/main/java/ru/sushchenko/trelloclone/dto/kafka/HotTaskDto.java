package ru.sushchenko.trelloclone.dto.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import ru.sushchenko.trelloclone.entity.enums.Priority;
import ru.sushchenko.trelloclone.entity.enums.Status;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class HotTaskDto {
    private String name;
    private String status;
    private String priority;
    private Date endDate;
}
