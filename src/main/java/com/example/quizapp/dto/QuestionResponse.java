package com.example.quizapp.dto;

import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.QuestionOption;
import com.example.quizapp.enums.QuestionType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A question as shown to a user attempting a quiz. ONE_LINE questions have
 * no options (their single "option" IS the correct answer, so it must never
 * be sent to the client); SINGLE_CHOICE / TRUE_FALSE questions include their
 * options with the correct-answer flag stripped out.
 */
public class QuestionResponse {

    private Long id;
    private String questionText;
    private QuestionType questionType;
    private Integer marks;
    private List<OptionResponse> options;

    public QuestionResponse() {
    }

    public QuestionResponse(Long id, String questionText, QuestionType questionType, Integer marks, List<OptionResponse> options) {
        this.id = id;
        this.questionText = questionText;
        this.questionType = questionType;
        this.marks = marks;
        this.options = options;
    }

    public static QuestionResponse from(Question question) {
        List<OptionResponse> options;
        if (question.getQuestionType() == QuestionType.SINGLE_CHOICE
                || question.getQuestionType() == QuestionType.TRUE_FALSE) {
            options = question.getOptions().stream()
                    .map(OptionResponse::from)
                    .collect(Collectors.toList());
        } else {
            options = Collections.emptyList();
        }
        return new QuestionResponse(question.getId(), question.getQuestionText(),
                question.getQuestionType(), question.getMarks(), options);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<OptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<OptionResponse> options) {
        this.options = options;
    }
}
