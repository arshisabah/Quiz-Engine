package com.example.quizapp.dto;

import com.example.quizapp.entity.Quiz;
import com.example.quizapp.entity.Question;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Full quiz payload for a user attempting it: basic quiz info plus its
 * sanitized questions (no correct answers). Used by GET /api/quizzes/{quizId}.
 */
public class QuizDetailResponse {

    private Long id;
    private String title;
    private String description;
    private String subject;
    private List<QuestionResponse> questions;

    public QuizDetailResponse() {
    }

    public QuizDetailResponse(Long id, String title, String description, String subject, List<QuestionResponse> questions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.questions = questions;
    }

    public static QuizDetailResponse from(Quiz quiz, List<Question> questions) {
        List<QuestionResponse> questionResponses = questions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
        return new QuizDetailResponse(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getSubject(), questionResponses);
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

    public List<QuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponse> questions) {
        this.questions = questions;
    }
}
