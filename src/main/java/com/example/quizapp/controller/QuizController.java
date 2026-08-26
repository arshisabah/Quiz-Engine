package com.example.quizapp.controller;

import com.example.quizapp.dto.QuizDetailResponse;
import com.example.quizapp.dto.QuizResponse;
import com.example.quizapp.dto.QuizResultResponse;
import com.example.quizapp.dto.SubmitQuizRequest;
import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.service.QuestionService;
import com.example.quizapp.service.QuizService;
import com.example.quizapp.service.QuizSubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final QuizSubmissionService quizSubmissionService;

    public QuizController(QuizService quizService, QuestionService questionService, QuizSubmissionService quizSubmissionService) {
        this.quizService = quizService;
        this.questionService = questionService;
        this.quizSubmissionService = quizSubmissionService;
    }

    @GetMapping("/quizzes")
    public List<QuizResponse> getAvailableQuizzes() {
        return quizService.getAllQuizzes().stream()
                .map(QuizResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/quizzes/{quizId}")
    public QuizDetailResponse getQuiz(@PathVariable Long quizId) {
        Quiz quiz = quizService.getQuizById(quizId);
        List<Question> questions = questionService.getQuestionsForQuiz(quizId);
        // QuizDetailResponse/QuestionResponse strip out correct-answer data
        // before it ever reaches the client - see their Javadoc.
        return QuizDetailResponse.from(quiz, questions);
    }

    @PostMapping("/quizzes/{quizId}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(@PathVariable Long quizId,
                                                       @Valid @RequestBody SubmitQuizRequest request) {
        QuizResultResponse result = quizSubmissionService.submitQuiz(quizId, request);
        return ResponseEntity.ok(result);
    }
}
