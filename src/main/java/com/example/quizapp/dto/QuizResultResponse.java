package com.example.quizapp.dto;

public class QuizResultResponse {

    private Long attemptId;
    private Long quizId;
    private Integer score;
    private Integer totalMarks;
    private String message;

    public QuizResultResponse() {
    }

    public QuizResultResponse(Long attemptId, Long quizId, Integer score, Integer totalMarks, String message) {
        this.attemptId = attemptId;
        this.quizId = quizId;
        this.score = score;
        this.totalMarks = totalMarks;
        this.message = message;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
