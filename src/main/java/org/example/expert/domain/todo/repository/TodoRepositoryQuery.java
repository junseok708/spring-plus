package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepositoryQuery {
    Page<Todo> findAllTodos(Pageable pageable, String weather, LocalDateTime modifiedAt, LocalDateTime lastModifiedAt);

    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}
