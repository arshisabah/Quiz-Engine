package com.example.quizapp.service;

import com.example.quizapp.dto.QuizResultResponse;
import com.example.quizapp.dto.SubmitQuizRequest;
import com.example.quizapp.dto.SubmittedAnswerRequest;
import com.example.quizapp.entity.*;
import com.example.quizapp.enums.QuestionType;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuestionOptionRepository;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizAttemptRepository;
import com.example.quizapp.repository.UserAnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class QuizSubmissionService {

    private final QuizService quizService;
    private final UserService userService;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;

    public QuizSubmissionService(QuizService quizService,
                                UserService userService,
                                QuestionRepository questionRepository,
                                QuestionOptionRepository questionOptionRepository,
                                QuizAttemptRepository quizAttemptRepository,
                                UserAnswerRepository userAnswerRepository) {
        this.quizService = quizService;
        this.userService = userService;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userAnswerRepository = userAnswerRepository;
    }

    @Transactional
    public QuizResultResponse submitQuiz(Long quizId, SubmitQuizRequest request) {
        Quiz quiz = quizService.getQuizById(quizId);
        User user = userService.getUserById(request.getUserId());

        List<Question> questions = questionRepository.findByQuiz(quiz);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("Quiz has no questions");
        }

        Map<Long, Question> questionMap = new HashMap<>();
        for (Question question : questions) {
            questionMap.put(question.getId(), question);
        }

        validateAnswers(request.getAnswers(), questionMap);

        int totalMarks = questions.stream().mapToInt(Question::getMarks).sum();
        int score = 0;

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setTotalMarks(totalMarks);
        attempt.setScore(0);
        attempt = quizAttemptRepository.save(attempt);

        for (SubmittedAnswerRequest answerRequest : request.getAnswers()) {
            Question question = questionMap.get(answerRequest.getQuestionId());
            if (question == null) {
                throw new ResourceNotFoundException("Question not found with id: " + answerRequest.getQuestionId());
            }

            boolean correct = false;
            int marksObtained = 0;

            if (question.getQuestionType() == QuestionType.SINGLE_CHOICE) {
                Long selectedOptionId = null;
                try {
                    selectedOptionId = Long.parseLong(answerRequest.getAnswer());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid answer for SINGLE_CHOICE question: " + answerRequest.getQuestionId());
                }

                List<QuestionOption> options = questionOptionRepository.findByQuestion(question);
                QuestionOption correctOption = options.stream()
                        .filter(QuestionOption::isCorrect)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No correct answer configured for question: " + question.getId()));

                correct = selectedOptionId.equals(correctOption.getId());
                marksObtained = correct ? question.getMarks() : 0;

            } else if (question.getQuestionType() == QuestionType.TRUE_FALSE) {
                String normalizedAnswer = normalizeAnswer(answerRequest.getAnswer());
                List<QuestionOption> options = questionOptionRepository.findByQuestion(question);
                String correctAnswer = options.stream()
                        .filter(QuestionOption::isCorrect)
                        .map(option -> normalizeAnswer(option.getOptionText()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No correct answer configured for question: " + question.getId()));

                correct = normalizedAnswer.equals(correctAnswer);
                marksObtained = correct ? question.getMarks() : 0;

            } else if (question.getQuestionType() == QuestionType.ONE_LINE) {
                List<QuestionOption> options = questionOptionRepository.findByQuestion(question);
                String expected = options.stream()
                        .filter(QuestionOption::isCorrect)
                        .map(option -> normalizeAnswer(option.getOptionText()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No correct answer configured for question: " + question.getId()));

                String submitted = normalizeAnswer(answerRequest.getAnswer());
                correct = submitted.equals(expected);
                marksObtained = correct ? question.getMarks() : 0;

            } else {
                throw new IllegalArgumentException("Invalid question type: " + question.getQuestionType());
            }

            if (correct) {
                score += marksObtained;
            }

            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setAttempt(attempt);
            userAnswer.setQuestion(question);
            userAnswer.setAnswer(answerRequest.getAnswer());
            userAnswer.setCorrect(correct);
            userAnswer.setMarksObtained(marksObtained);
            userAnswerRepository.save(userAnswer);
        }

        attempt.setScore(score);
        attempt = quizAttemptRepository.save(attempt);

        return new QuizResultResponse(attempt.getId(), quiz.getId(), score, totalMarks, "Quiz submitted successfully");
    }

    private void validateAnswers(List<SubmittedAnswerRequest> answers, Map<Long, Question> questionMap) {
        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Invalid answer");
        }

        Set<Long> seenQuestionIds = new HashSet<>();
        for (SubmittedAnswerRequest submittedAnswer : answers) {
            if (submittedAnswer.getQuestionId() == null || submittedAnswer.getAnswer() == null || submittedAnswer.getAnswer().isBlank()) {
                throw new IllegalArgumentException("Invalid answer");
            }
            if (!questionMap.containsKey(submittedAnswer.getQuestionId())) {
                throw new ResourceNotFoundException("Question not found with id: " + submittedAnswer.getQuestionId());
            }
            // Prevent the same question from being answered more than once in a
            // single submission, which would otherwise let a user double-count
            // marks for that question.
            if (!seenQuestionIds.add(submittedAnswer.getQuestionId())) {
                throw new IllegalArgumentException("Duplicate answer submitted for question id: " + submittedAnswer.getQuestionId());
            }
        }
    }

    private String normalizeAnswer(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
