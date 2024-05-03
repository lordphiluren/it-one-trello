package ru.sushchenko.trelloclone.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sushchenko.trelloclone.entity.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepo extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "task-all-relations")
    List<Task> findAll();
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "task-all-relations")
    Page<Task> findAll(Specification<Task> spec, Pageable pageable);
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "task-all-relations")
    Optional<Task> findById(UUID id);
}
