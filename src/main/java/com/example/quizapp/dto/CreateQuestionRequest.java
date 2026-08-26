package com.example.quizapp.dto;

import com.example.quizapp.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateQuestionRequest {

    @NotBlank
    private String questionText;

    @NotNull
    private QuestionType questionType;

    @NotNull
    @Min(1)
    private Integer marks;

    @Valid
    private List<QuestionOptionRequest> options;

    private String correctAnswer;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public List<QuestionOptionRequest> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOptionRequest> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
