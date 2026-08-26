package com.example.quizapp.dto;

import jakarta.validation.constraints.NotBlank;

public class QuestionOptionRequest {

    @NotBlank
    private String optionText;

    private boolean isCorrect;

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
