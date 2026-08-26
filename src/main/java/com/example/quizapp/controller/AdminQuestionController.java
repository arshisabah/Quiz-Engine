package com.example.quizapp.controller;

import com.example.quizapp.dto.CreateQuestionRequest;
import com.example.quizapp.entity.Question;
import com.example.quizapp.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminQuestionController {

    private final QuestionService questionService;

    public AdminQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<Question> addQuestion(@PathVariable Long quizId,
                                               @Valid @RequestBody CreateQuestionRequest request) {
        Question question = questionService.addQuestion(quizId, request);
        return new ResponseEntity<>(question, HttpStatus.CREATED);
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok("Question deleted successfully");
    }
}
