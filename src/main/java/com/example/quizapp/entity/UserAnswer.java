package com.example.quizapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_answers")
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @JsonIgnore prevents QuizAttempt -> answers -> UserAnswer.attempt ->
    // answers -> ... infinite recursion if a QuizAttempt is ever serialized.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @NotBlank
    @Column(nullable = false)
    private String answer;

    @NotNull
    @Column(nullable = false)
    private Boolean correct;

    @NotNull
    @Column(name = "marks_obtained", nullable = false)
    private Integer marksObtained;

    public UserAnswer() {
    }

    public UserAnswer(QuizAttempt attempt, Question question, String answer, Boolean correct, Integer marksObtained) {
        this.attempt = attempt;
        this.question = question;
        this.answer = answer;
        this.correct = correct;
        this.marksObtained = marksObtained;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuizAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(QuizAttempt attempt) {
        this.attempt = attempt;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public Integer getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Integer marksObtained) {
        this.marksObtained = marksObtained;
    }
}
