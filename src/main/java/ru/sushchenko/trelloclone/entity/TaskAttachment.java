package ru.sushchenko.trelloclone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sushchenko.trelloclone.entity.id.TaskAttachmentKey;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_attachment")
public class TaskAttachment {
    @EmbeddedId
    private TaskAttachmentKey id;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;
}
