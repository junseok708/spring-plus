package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoSearchResponse {

    private String title;
    private Long assigneeCount;
    private Long completedCount;

    public TodoSearchResponse(String title, Long assigneeCount, Long completedCount) {
        this.title = title;
        this.assigneeCount = assigneeCount;
        this.completedCount = completedCount;
    }
}
