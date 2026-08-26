package com.example.quizapp.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SubmitQuizRequest {

    @NotNull
    private Long userId;

    @NotNull
    private List<SubmittedAnswerRequest> answers;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<SubmittedAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SubmittedAnswerRequest> answers) {
        this.answers = answers;
    }
}
