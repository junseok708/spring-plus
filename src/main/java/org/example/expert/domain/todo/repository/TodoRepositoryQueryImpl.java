package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery{
    private final JPAQueryFactory queryFactory;
    QTodo todo =QTodo.todo;

    @Override
    public Page<Todo> findAllTodos(Pageable pageable) {
        List<Todo> todos = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user)
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


    /*private BooleanExpression eqWeather(String weather){
        if(CollectionUtils.isEmpty(weather)){
            return null;
        }
        return todo.weather.in(weather);
    }*/
}
