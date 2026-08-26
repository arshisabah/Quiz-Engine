package com.example.quizapp.service;

import com.example.quizapp.dto.CreateQuizRequest;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.entity.User;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuizRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserService userService;

    public QuizService(QuizRepository quizRepository, UserService userService) {
        this.quizRepository = quizRepository;
        this.userService = userService;
    }

    public Quiz createQuiz(@Valid CreateQuizRequest request) {
        User creator = userService.getUserById(request.getCreatedBy());

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setSubject(request.getSubject());
        quiz.setCreatedBy(creator);

        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
    }
}
