package ru.sushchenko.trelloclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sushchenko.trelloclone.entity.CheckItem;

import java.util.UUID;

public interface CheckItemRepo extends JpaRepository<CheckItem, UUID> {
}
