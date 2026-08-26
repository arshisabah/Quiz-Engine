package com.example.quizapp.service;

import com.example.quizapp.dto.CreateQuestionRequest;
import com.example.quizapp.dto.QuestionOptionRequest;
import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.QuestionOption;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.enums.QuestionType;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuestionOptionRepository;
import com.example.quizapp.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuizService quizService;

    public QuestionService(QuestionRepository questionRepository,
                          QuestionOptionRepository questionOptionRepository,
                          QuizService quizService) {
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.quizService = quizService;
    }

    public Question addQuestion(Long quizId, CreateQuestionRequest request) {
        Quiz quiz = quizService.getQuizById(quizId);

        if (request.getQuestionType() == null) {
            throw new IllegalArgumentException("Invalid question type");
        }

        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setMarks(request.getMarks());

        Question savedQuestion = questionRepository.save(question);

        if (request.getQuestionType() == QuestionType.SINGLE_CHOICE || request.getQuestionType() == QuestionType.TRUE_FALSE) {
            if (request.getOptions() == null || request.getOptions().isEmpty()) {
                throw new IllegalArgumentException("Options are required for this question type");
            }

            List<QuestionOption> options = new ArrayList<>();
            int correctCount = 0;
            for (QuestionOptionRequest optionRequest : request.getOptions()) {
                QuestionOption option = new QuestionOption(savedQuestion, optionRequest.getOptionText(), optionRequest.isCorrect());
                options.add(option);
                if (optionRequest.isCorrect()) {
                    correctCount++;
                }
            }

            if (request.getQuestionType() == QuestionType.SINGLE_CHOICE && correctCount != 1) {
                throw new IllegalArgumentException("Exactly one option must be correct for SINGLE_CHOICE");
            }

            if (request.getQuestionType() == QuestionType.TRUE_FALSE) {
                boolean hasTrue = options.stream().anyMatch(opt -> opt.getOptionText().equalsIgnoreCase("True") && opt.isCorrect());
                boolean hasFalse = options.stream().anyMatch(opt -> opt.getOptionText().equalsIgnoreCase("False") && opt.isCorrect());
                if (!hasTrue && !hasFalse) {
                    throw new IllegalArgumentException("TRUE_FALSE question must have a correct True/False option");
                }
                // Exactly one option may be correct, same rule as SINGLE_CHOICE -
                // otherwise a question could be configured as unanswerable
                // (both True and False marked correct, or vice versa).
                if (correctCount != 1) {
                    throw new IllegalArgumentException("Exactly one option must be correct for TRUE_FALSE");
                }
            }

            savedQuestion.setOptions(options);
            questionOptionRepository.saveAll(options);
        }

        if (request.getQuestionType() == QuestionType.ONE_LINE) {
            if (request.getCorrectAnswer() == null || request.getCorrectAnswer().isBlank()) {
                throw new IllegalArgumentException("Correct answer is required for ONE_LINE question");
            }

            QuestionOption correctOption = new QuestionOption(savedQuestion, request.getCorrectAnswer(), true);
            savedQuestion.setOptions(List.of(correctOption));
            questionOptionRepository.save(correctOption);
        }

        return savedQuestion;
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        questionRepository.delete(question);
    }

    public List<Question> getQuestionsForQuiz(Long quizId) {
        Quiz quiz = quizService.getQuizById(quizId);
        return questionRepository.findByQuiz(quiz);
    }
}
