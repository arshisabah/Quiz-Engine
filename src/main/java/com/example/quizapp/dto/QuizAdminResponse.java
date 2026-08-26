package com.example.quizapp.dto;

import com.example.quizapp.entity.Quiz;

/**
 * Quiz view for the admin APIs (create/list/get by id). Same basic fields as
 * QuizResponse plus who created the quiz - useful for admins, not needed by
 * the user-facing quiz APIs. Does not include the nested question list;
 * questions are managed through the dedicated question endpoints.
 */
public class QuizAdminResponse {

    private Long id;
    private String title;
    private String description;
    private String subject;
    private Long createdById;
    private String createdByName;

    public QuizAdminResponse() {
    }

    public QuizAdminResponse(Long id, String title, String description, String subject, Long createdById, String createdByName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.createdById = createdById;
        this.createdByName = createdByName;
    }

    public static QuizAdminResponse from(Quiz quiz) {
        Long createdById = quiz.getCreatedBy() != null ? quiz.getCreatedBy().getId() : null;
        String createdByName = quiz.getCreatedBy() != null ? quiz.getCreatedBy().getName() : null;
        return new QuizAdminResponse(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getSubject(), createdById, createdByName);
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

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}
