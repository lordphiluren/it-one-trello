package ru.sushchenko.trelloclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sushchenko.trelloclone.entity.TaskAttachment;

import java.util.UUID;

@Repository
public interface AttachmentRepo extends JpaRepository<TaskAttachment, UUID> {
}
