package ru.sushchenko.trelloclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sushchenko.trelloclone.entity.Tag;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.id.TaskTagKey;

import java.util.UUID;

@Repository
public interface TagRepo extends JpaRepository<Tag, TaskTagKey> {
    void deleteAllByTask(Task task);
}
