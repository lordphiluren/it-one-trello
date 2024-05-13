package ru.sushchenko.trelloclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sushchenko.trelloclone.entity.Tag;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.id.TaskTagKey;

@Repository
public interface TagRepo extends JpaRepository<Tag, TaskTagKey> {
    void deleteAllByTask(Task task);
}
