package ru.sushchenko.trelloclone.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import ru.sushchenko.trelloclone.entity.Tag;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.enums.Priority;
import ru.sushchenko.trelloclone.entity.enums.Status;
import ru.sushchenko.trelloclone.entity.id.TaskAttachmentKey;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private Priority priority;
    private Status status;
    private UserResponse creator;
    private Set<UserResponse> executors;
    private Set<CommentResponse> comments;
    private Set<String> attachments;
    private Set<String> tags;
    private Date endDate;
    private Date createdAt;
    private Date updatedAt;
    private Date closedAt;
}
