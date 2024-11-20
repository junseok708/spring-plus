package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery {
    private final JPAQueryFactory queryFactory;
    QTodo todo = QTodo.todo;
    QUser user = QUser.user;
    QComment comment = QComment.comment;
    QManager manager = QManager.manager;

    @Override
    public Page<Todo> findAllTodos(
            Pageable pageable, String weather, LocalDateTime modifiedAt, LocalDateTime lastModifiedAt) {
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

    @Override
    public Page<TodoSearchResponse> findSearchTodos(
            Pageable pageable, String title, LocalDateTime dateTime, LocalDateTime lastDateTime, String nickname) {
        List<TodoSearchResponse> todos = queryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        manager.id.countDistinct(),
                        comment.id.count()
                ))
                .from(todo)
                .leftJoin(todo.user,user)
                .leftJoin(todo.comments,comment)
                .leftJoin(todo.managers,manager)
                .where(
                        eqTitle(title),
                        eqCreateAtPeriod(dateTime, lastDateTime),
                        eqNickname(nickname)
                )
                .groupBy(todo.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(todos);
    }


    private BooleanExpression eqWeather(String weather) {
        return weather != null ? todo.weather.in(weather) : null;
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

    private BooleanExpression eqTitle(String title) {

        return title != null ? todo.title.like("%"+title+"%") : null;
    }

    private BooleanExpression eqCreateAtPeriod(LocalDateTime dateTime, LocalDateTime lastDateTime) {
        if (dateTime == null || lastDateTime == null) {
            return null;
        }
        return todo.createdAt.goe(dateTime).and(todo.createdAt.loe(lastDateTime));
    }

    private BooleanExpression eqNickname(String nickname) {

        return nickname != null ? user.nickname.like("%"+nickname+"%") : null;
    }
}
