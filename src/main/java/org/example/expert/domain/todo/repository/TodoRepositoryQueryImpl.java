package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery {
    private final JPAQueryFactory queryFactory;
    QTodo todo = QTodo.todo;

    @Override
    public Page<Todo> findAllTodos(Pageable pageable, String weather, LocalDateTime modifiedAt, LocalDateTime lastModifiedAt) {
        List<Todo> todos = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user)
                .where(
                        eqWeather(weather),
                        eqModifiedAt(modifiedAt),
                        eqModifiedAtPeriod(modifiedAt, lastModifiedAt)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(todos);
    }

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user)
                .where(todo.id.eq(todoId))
                .fetchOne());
    }


    private BooleanExpression eqWeather(String weather) {
        if (StringUtils.isEmpty(weather)) {
            return null;
        }
        return todo.weather.in(weather);
    }

    private BooleanExpression eqModifiedAt(LocalDateTime modifiedAt) {
        if (modifiedAt == null) {
            return null;
        }
        return todo.modifiedAt.goe(modifiedAt);
    }

    private BooleanExpression eqModifiedAtPeriod(LocalDateTime modifiedAt, LocalDateTime lastModifiedAt) {
        if (lastModifiedAt == null || modifiedAt == null) {
            return null;
        }
        return todo.modifiedAt.goe(modifiedAt).and(todo.modifiedAt.loe(lastModifiedAt));
    }
}
