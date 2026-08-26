package com.example.quizapp.controller;

import com.example.quizapp.dto.CreateQuizRequest;
import com.example.quizapp.dto.QuizAdminResponse;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminQuizController {

    private final QuizService quizService;

    public AdminQuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/quizzes")
    public ResponseEntity<QuizAdminResponse> createQuiz(@Valid @RequestBody CreateQuizRequest request) {
        Quiz quiz = quizService.createQuiz(request);
        return new ResponseEntity<>(QuizAdminResponse.from(quiz), HttpStatus.CREATED);
    }

    @GetMapping("/quizzes")
    public List<QuizAdminResponse> getAllQuizzes() {
        return quizService.getAllQuizzes().stream()
                .map(QuizAdminResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/quizzes/{quizId}")
    public QuizAdminResponse getQuizById(@PathVariable Long quizId) {
        return QuizAdminResponse.from(quizService.getQuizById(quizId));
    }
}
