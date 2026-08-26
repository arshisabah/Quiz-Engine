# Generic Quiz Application

## 1. Project Overview
This is a simple beginner-friendly Spring Boot backend for a generic quiz application. It supports two roles: Admin and User. The Admin can create quizzes and add questions; the User can view quizzes and submit answers to receive a score.

## 2. Technologies Used
- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- MySQL
- Maven
- Hibernate / JPA

## 3. Database Setup
Create the database before running the application:

```sql
CREATE DATABASE quiz_app;
```

Then update the username and password in [src/main/resources/application.properties](src/main/resources/application.properties).

## 4. How to Run
1. Make sure MySQL is running.
2. Create the database `quiz_app`.
3. Update credentials in `application.properties`.
4. Run:

```bash
mvn spring-boot:run
```

## 5. Database Tables
The application uses these main tables:
- users
- quizzes
- questions
- question_options
- quiz_attempts
- user_answers

## 6. API Endpoints

### Admin APIs
- `POST /api/users`
- `POST /api/admin/quizzes`
- `GET /api/admin/quizzes`
- `GET /api/admin/quizzes/{quizId}`
- `POST /api/admin/quizzes/{quizId}/questions`
- `DELETE /api/admin/questions/{questionId}`

### User APIs
- `GET /api/quizzes`
- `GET /api/quizzes/{quizId}`
- `POST /api/quizzes/{quizId}/submit`

## 7. Sample Requests

### Create User
```http
POST /api/users
Content-Type: application/json

{
  "name": "John",
  "email": "john@gmail.com",
  "role": "USER"
}
```

### Create Quiz
```http
POST /api/admin/quizzes
Content-Type: application/json

{
  "title": "Java Basics",
  "description": "Basic Java quiz",
  "subject": "Programming",
  "createdBy": 1
}
```

### Add Question
```http
POST /api/admin/quizzes/1/questions
Content-Type: application/json

{
  "questionText": "Which keyword is used for inheritance?",
  "questionType": "SINGLE_CHOICE",
  "marks": 1,
  "options": [
    { "optionText": "extends", "isCorrect": true },
    { "optionText": "implements", "isCorrect": false },
    { "optionText": "inherits", "isCorrect": false },
    { "optionText": "super", "isCorrect": false }
  ]
}
```

### Submit Quiz
```http
POST /api/quizzes/1/submit
Content-Type: application/json

{
  "userId": 2,
  "answers": [
    { "questionId": 1, "answer": "1" },
    { "questionId": 2, "answer": "Paris" },
    { "questionId": 3, "answer": "True" }
  ]
}
```

## 8. Sample Responses

### Available quizzes (GET /api/quizzes)
```json
[
  {
    "id": 1,
    "title": "Java Basics",
    "description": "Basic Java programming",
    "subject": "Programming"
  }
]
```

### Quiz detail (GET /api/quizzes/{quizId})
```json
{
  "id": 1,
  "title": "Java Basics",
  "description": "Basic Java programming",
  "subject": "Programming",
  "questions": [
    {
      "id": 1,
      "questionText": "Which keyword is used for inheritance?",
      "questionType": "SINGLE_CHOICE",
      "marks": 1,
      "options": [
        { "id": 1, "optionText": "extends" },
        { "id": 2, "optionText": "implements" },
        { "id": 3, "optionText": "inherits" },
        { "id": 4, "optionText": "super" }
      ]
    }
  ]
}
```
Correct answers never appear in this response.

### Quiz result
```json
{
  "attemptId": 1,
  "quizId": 1,
  "score": 8,
  "totalMarks": 10,
  "message": "Quiz submitted successfully"
}
```

### Error response (e.g. quiz/question/user not found, invalid answer)
```json
{
  "timestamp": "2026-08-26T10:15:30Z",
  "status": 404,
  "error": "Not Found",
  "message": "Quiz not found with id: 99"
}
```

## 9. Quiz Submission Flow
1. User submits answers for a quiz.
2. Backend finds the quiz and its questions.
3. It compares each answer against the correct answer in the database.
4. It calculates the score.
5. It saves the `QuizAttempt` record.
6. It saves each `UserAnswer` record.
7. It returns the result to the user.

---

## Important classes
### `User`
Represents the `users` table. Stores the user name, email, and role.

### `Quiz`
Represents the `quizzes` table. Stores the title, description, subject, and who created the quiz.

### `Question`
Represents the `questions` table. Each question belongs to a quiz and has a question type and marks.

### `QuestionOption`
Represents the `question_options` table. Stores possible choices and identifies the correct option.

### `QuizAttempt`
Stores a user submission and the final score for that attempt.

### `UserAnswer`
Stores the answer submitted by the user for one question and whether it was correct.

### `QuizSubmissionService`
Contains the evaluation logic: validate answers, compare against correct answers, calculate marks, save the attempt, and return the result.

### `ResourceNotFoundException`
Used when a user, quiz, or question cannot be found.
