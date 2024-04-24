package ru.sushchenko.trelloclone.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TaskAttachmentKey implements Serializable {
    @Column(name = "task_id")
    private Long taskId;
    @Column(name = "url")
    private String url;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskAttachmentKey that = (TaskAttachmentKey) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, taskId);
    }
}
