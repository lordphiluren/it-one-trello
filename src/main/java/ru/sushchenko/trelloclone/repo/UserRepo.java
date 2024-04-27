package ru.sushchenko.trelloclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sushchenko.trelloclone.entity.User;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Set<User> findByIdIn(Set<UUID> ids);
}
