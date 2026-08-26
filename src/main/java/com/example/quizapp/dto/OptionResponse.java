package com.example.quizapp.dto;

import com.example.quizapp.entity.QuestionOption;

/**
 * An option as shown to users while taking a quiz. Intentionally has no
 * "correct" field - the correct answer must never leave the server (see
 * README section 13 / guide.md section 13).
 */
public class OptionResponse {

    private Long id;
    private String optionText;

    public OptionResponse() {
    }

    public OptionResponse(Long id, String optionText) {
        this.id = id;
        this.optionText = optionText;
    }

    public static OptionResponse from(QuestionOption option) {
        return new OptionResponse(option.getId(), option.getOptionText());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
}
