package com.example.quizapp.repository;

import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuiz(Quiz quiz);
}
