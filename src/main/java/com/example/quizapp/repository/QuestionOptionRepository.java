package com.example.quizapp.repository;

import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findByQuestion(Question question);
}
