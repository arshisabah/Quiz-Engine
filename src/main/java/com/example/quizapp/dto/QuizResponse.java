package com.example.quizapp.dto;

import com.example.quizapp.entity.Quiz;

/**
 * Basic quiz info returned to users: id, title, description, subject.
 * Deliberately excludes createdBy and questions so that GET /api/quizzes
 * (list of available quizzes) matches the shape described in the README/guide
 * instead of dumping the full entity graph.
 */
public class QuizResponse {

    private Long id;
    private String title;
    private String description;
    private String subject;

    public QuizResponse() {
    }

    public QuizResponse(Long id, String title, String description, String subject) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
    }

    public static QuizResponse from(Quiz quiz) {
        return new QuizResponse(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getSubject());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
