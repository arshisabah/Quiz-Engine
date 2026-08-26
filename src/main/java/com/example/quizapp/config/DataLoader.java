package com.example.quizapp.config;

import com.example.quizapp.entity.*;
import com.example.quizapp.enums.QuestionType;
import com.example.quizapp.enums.Role;
import com.example.quizapp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;

    public DataLoader(UserRepository userRepository,
                      QuizRepository quizRepository,
                      QuestionRepository questionRepository,
                      QuestionOptionRepository questionOptionRepository) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = userRepository.save(new User("Admin", "admin@gmail.com", Role.ADMIN));
        User user = userRepository.save(new User("John", "john@gmail.com", Role.USER));

        Quiz quiz = quizRepository.save(new Quiz("Java Basics", "Basic Java programming", "Programming", admin));

        Question q1 = questionRepository.save(new Question(quiz, "Which keyword is used for inheritance?", QuestionType.SINGLE_CHOICE, 1));
        Question q2 = questionRepository.save(new Question(quiz, "What is JVM?", QuestionType.ONE_LINE, 2));
        Question q3 = questionRepository.save(new Question(quiz, "Java supports OOP.", QuestionType.TRUE_FALSE, 1));
        Question q4 = questionRepository.save(new Question(quiz, "Which collection is ordered and stores unique elements?", QuestionType.SINGLE_CHOICE, 1));

        questionOptionRepository.saveAll(List.of(
                new QuestionOption(q1, "extends", true),
                new QuestionOption(q1, "implements", false),
                new QuestionOption(q1, "inherits", false),
                new QuestionOption(q1, "super", false),

                new QuestionOption(q2, "Java Virtual Machine", true),

                new QuestionOption(q3, "True", true),
                new QuestionOption(q3, "False", false),

                new QuestionOption(q4, "ArrayList", false),
                new QuestionOption(q4, "HashSet", true),
                new QuestionOption(q4, "LinkedList", false),
                new QuestionOption(q4, "HashMap", false)
        ));
    }
}
