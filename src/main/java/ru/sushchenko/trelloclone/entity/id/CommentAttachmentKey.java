package ru.sushchenko.trelloclone.entity.id;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CommentAttachmentKey implements Serializable {
    @Column(name = "comment_id")
    private UUID commentId;
    @Column(name = "url")
    private String url;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentAttachmentKey that = (CommentAttachmentKey) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(commentId, that.commentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, commentId);
    }
}