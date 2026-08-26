# Generic Quiz Application — Spring Boot + Java 17 + MySQL

Act as an expert Java Spring Boot developer and build a **simple generic Quiz Application backend** for learning purposes.

The application must allow an **Admin to create quizzes with different question types** and allow a **User to attempt quizzes and receive their score after submission**.

The project must be simple, clean, beginner-friendly, and easy to understand.

Do NOT over-engineer the solution.

---

## 1. Technology Stack

Use only:

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* MySQL
* Maven
* Lombok if useful
* Bean Validation if useful

Do NOT use:

* Microservices
* Kafka
* Redis
* Docker
* Kubernetes
* Elasticsearch
* Complex security
* JWT unless absolutely necessary
* Event-driven architecture
* CQRS
* Complex design patterns
* External APIs

The goal is to understand:

* REST APIs
* Spring Boot
* Controllers
* Services
* Repositories
* Entities
* DTOs
* JPA relationships
* MySQL
* Basic business logic

---

# 2. Application Concept

The application has two logical roles:

```text
ADMIN
USER
```

The Admin creates quizzes.

The User views available quizzes, attempts a quiz, submits answers, and receives the score.

The quiz must be generic.

For example, the Admin should be able to create:

```text
Java Quiz
History Quiz
Mathematics Quiz
Science Quiz
General Knowledge Quiz
```

The backend should NOT contain subject-specific logic.

---

# 3. Supported Question Types

Initially support only these three types:

```text
SINGLE_CHOICE
ONE_LINE
TRUE_FALSE
```

### SINGLE_CHOICE

Example:

```text
What is the capital of France?

A. London
B. Paris
C. Rome
D. Berlin
```

The user selects one option.

### ONE_LINE

Example:

```text
What is the capital of France?

Answer: Paris
```

The user enters a text answer.

### TRUE_FALSE

Example:

```text
Java is a programming language.

True
False
```

The user selects one answer.

Design the database so that additional question types can be added later, but DO NOT implement them now.

---

# 4. Keep Database Schema Simple

Use only the important fields.

Do NOT create unnecessary tables or columns.

Use these main tables:

```text
users
quizzes
questions
question_options
quiz_attempts
user_answers
```

---

# 5. Users Table

Create:

```text
users
```

Fields:

```text
id
name
email
role
```

Example:

```text
1 | Arshi | arshi@gmail.com | ADMIN
2 | John  | john@gmail.com  | USER
```

Use:

```text
role = ADMIN
role = USER
```

Do not implement complicated authentication for this learning project.

---

# 6. Quizzes Table

Create:

```text
quizzes
```

Fields:

```text
id
title
description
subject
created_by
```

Example:

```text
1
Java Basics
Basic Java programming quiz
Programming
1
```

`created_by` should reference the Admin user.

Do not add unnecessary fields such as:

```text
created_at
updated_at
version
deleted_at
published_by
```

unless they are actually needed.

---

# 7. Questions Table

Create:

```text
questions
```

Fields:

```text
id
quiz_id
question_text
question_type
marks
```

Example:

```text
1 | 1 | Which keyword is used for inheritance? | SINGLE_CHOICE | 1
2 | 1 | What is JVM?                         | ONE_LINE      | 2
3 | 1 | Java supports OOP.                    | TRUE_FALSE    | 1
```

`quiz_id` should reference `quizzes`.

`question_type` should be an enum:

```java
public enum QuestionType {
    SINGLE_CHOICE,
    ONE_LINE,
    TRUE_FALSE
}
```

---

# 8. Question Options Table

Create:

```text
question_options
```

Fields:

```text
id
question_id
option_text
is_correct
```

Example:

```text
1 | 1 | extends    | true
2 | 1 | implements | false
3 | 1 | inherits   | false
4 | 1 | super      | false
```

For `ONE_LINE` questions, options are not required.

For `TRUE_FALSE`, options can be:

```text
True
False
```

The correct option should be stored in the database.

---

# 9. Quiz Attempts Table

Create:

```text
quiz_attempts
```

Fields:

```text
id
quiz_id
user_id
score
total_marks
```

Example:

```text
1 | 1 | 2 | 8 | 10
```

This means:

```text
User 2
attempted Quiz 1
scored 8 out of 10
```

Keep it simple.

---

# 10. User Answers Table

Create:

```text
user_answers
```

Fields:

```text
id
attempt_id
question_id
answer
correct
marks_obtained
```

Example:

```text
1 | 1 | 101 | A      | true  | 1
2 | 1 | 102 | Paris  | true  | 2
3 | 1 | 103 | False  | false | 0
```

This table should store the user's submitted answer and evaluation result.

---

# 11. Entity Relationships

Keep relationships simple.

```text
User
 |
 +---- Quiz
 |
 +---- QuizAttempt
```

```text
Quiz
 |
 +---- Question
          |
          +---- QuestionOption
```

```text
QuizAttempt
 |
 +---- UserAnswer
```

Use JPA relationships where appropriate:

```text
@OneToMany
@ManyToOne
```

Do not create overly complicated bidirectional relationships everywhere.

Prefer simple relationships that are easy for a beginner to understand.

---

# 12. REST API

Create simple REST APIs.

## Admin APIs

### Create User

```http
POST /api/users
```

### Create Quiz

```http
POST /api/admin/quizzes
```

Request:

```json
{
  "title": "Java Basics",
  "description": "Basic Java quiz",
  "subject": "Programming",
  "createdBy": 1
}
```

---

### Get All Quizzes

```http
GET /api/admin/quizzes
```

---

### Get Quiz By ID

```http
GET /api/admin/quizzes/{quizId}
```

---

### Add Question

```http
POST /api/admin/quizzes/{quizId}/questions
```

For SINGLE_CHOICE:

```json
{
  "questionText": "Which keyword is used for inheritance?",
  "questionType": "SINGLE_CHOICE",
  "marks": 1,
  "options": [
    {
      "optionText": "extends",
      "isCorrect": true
    },
    {
      "optionText": "implements",
      "isCorrect": false
    },
    {
      "optionText": "inherits",
      "isCorrect": false
    },
    {
      "optionText": "super",
      "isCorrect": false
    }
  ]
}
```

For ONE_LINE:

```json
{
  "questionText": "What is the capital of France?",
  "questionType": "ONE_LINE",
  "marks": 2,
  "correctAnswer": "Paris"
}
```

For TRUE_FALSE:

```json
{
  "questionText": "Java supports object-oriented programming.",
  "questionType": "TRUE_FALSE",
  "marks": 1,
  "options": [
    {
      "optionText": "True",
      "isCorrect": true
    },
    {
      "optionText": "False",
      "isCorrect": false
    }
  ]
}
```

---

### Delete Question

```http
DELETE /api/admin/questions/{questionId}
```

---

# 13. User APIs

### Get Available Quizzes

```http
GET /api/quizzes
```

Return basic information:

```json
[
  {
    "id": 1,
    "title": "Java Basics",
    "description": "Basic Java quiz",
    "subject": "Programming"
  }
]
```

---

### Get Quiz

```http
GET /api/quizzes/{quizId}
```

IMPORTANT:

Do NOT return `isCorrect` to the user.

For example, return:

```json
{
  "id": 1,
  "title": "Java Basics",
  "questions": [
    {
      "id": 101,
      "questionText": "Which keyword is used for inheritance?",
      "questionType": "SINGLE_CHOICE",
      "marks": 1,
      "options": [
        {
          "id": 1,
          "optionText": "extends"
        },
        {
          "id": 2,
          "optionText": "implements"
        },
        {
          "id": 3,
          "optionText": "inherits"
        },
        {
          "id": 4,
          "optionText": "super"
        }
      ]
    }
  ]
}
```

The correct answer must remain on the server.

---

# 14. Submit Quiz

Create:

```http
POST /api/quizzes/{quizId}/submit
```

Request:

```json
{
  "userId": 2,
  "answers": [
    {
      "questionId": 101,
      "answer": "1"
    },
    {
      "questionId": 102,
      "answer": "Paris"
    },
    {
      "questionId": 103,
      "answer": "True"
    }
  ]
}
```

The backend must:

```text
Receive answers
       ↓
Find quiz
       ↓
Find questions
       ↓
Find correct answers
       ↓
Compare user answers
       ↓
Calculate marks
       ↓
Create QuizAttempt
       ↓
Create UserAnswers
       ↓
Return result
```

---

# 15. Evaluation Rules

## SINGLE_CHOICE

Compare the selected option ID with the correct option.

If correct:

```text
marks_obtained = question.marks
correct = true
```

Otherwise:

```text
marks_obtained = 0
correct = false
```

---

## TRUE_FALSE

Compare the submitted answer with the correct option.

Example:

```text
Correct answer = True
User answer = True
```

Result:

```text
correct = true
marks_obtained = question.marks
```

---

## ONE_LINE

Compare the submitted text with the correct answer.

Normalize the values before comparison:

```text
trim spaces
ignore case
```

Therefore:

```text
Paris
paris
 PARIS
```

should all be treated as the same answer.

For the learning project, do not implement fuzzy matching, AI evaluation, NLP, or similarity algorithms.

---

# 16. Result Response

After submission return:

```json
{
  "attemptId": 1,
  "quizId": 1,
  "score": 8,
  "totalMarks": 10,
  "message": "Quiz submitted successfully"
}
```

The frontend can display:

```text
Score: 8 / 10
```

---

# 17. Project Structure

Use a simple layered architecture:

```text
src/main/java/com/example/quizapp

controller
    AdminQuizController.java
    AdminQuestionController.java
    QuizController.java
    UserController.java

service
    QuizService.java
    QuestionService.java
    QuizSubmissionService.java
    UserService.java

repository
    UserRepository.java
    QuizRepository.java
    QuestionRepository.java
    QuestionOptionRepository.java
    QuizAttemptRepository.java
    UserAnswerRepository.java

entity
    User.java
    Quiz.java
    Question.java
    QuestionOption.java
    QuizAttempt.java
    UserAnswer.java

dto
    CreateQuizRequest.java
    CreateQuestionRequest.java
    SubmitQuizRequest.java
    QuizResultResponse.java

enums
    Role.java
    QuestionType.java

exception
    ResourceNotFoundException.java

QuizApplication.java
```

Keep the package structure understandable for beginners.

---

# 18. application.properties

Configure MySQL using:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quiz_app
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.jpa.properties.hibernate.format_sql=true
```

Do not hardcode the actual password.

Use a placeholder.

---

# 19. Error Handling

Create only one simple custom exception:

```java
ResourceNotFoundException
```

Handle common cases:

```text
Quiz not found
Question not found
User not found
Invalid question type
Invalid answer
```

Use appropriate HTTP responses:

```text
200 OK
201 CREATED
400 BAD REQUEST
404 NOT FOUND
```

Do not create a complicated global error framework.

---

# 20. Validation

Use basic validation.

For example:

```java
@NotBlank
private String title;
```

and:

```java
@NotNull
private QuestionType questionType;
```

Only add validation that is actually useful.

---

# 21. Important Business Rules

Implement these basic rules:

1. A quiz must have a title.
2. A question must belong to a quiz.
3. A question must have a question type.
4. A question must have marks.
5. SINGLE_CHOICE must have options.
6. TRUE_FALSE must have True/False options.
7. ONE_LINE must have a correct answer.
8. Only one option can be correct for SINGLE_CHOICE.
9. Correct answers must never be exposed through the User quiz API.
10. Score must be calculated on the backend.
11. Every submission creates a QuizAttempt.
12. Every submitted answer creates a UserAnswer.

---

# 22. Keep the Code Beginner-Friendly

Do NOT try to demonstrate every advanced Spring Boot feature.

Prefer simple code such as:

```java
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public List<Quiz> getQuizzes() {
        return quizService.getAllQuizzes();
    }
}
```

Services should contain business logic.

Repositories should handle database access.

Controllers should handle HTTP requests.

Entities should represent database tables.

DTOs should represent API request/response data.

---

# 23. Important Evaluation Service

Create one main service:

```java
QuizSubmissionService
```

It should contain the main flow:

```text
submitQuiz()
    ↓
loadQuiz()
    ↓
validateAnswers()
    ↓
evaluateAnswers()
    ↓
calculateScore()
    ↓
saveAttempt()
    ↓
saveUserAnswers()
    ↓
returnResult()
```

Keep the evaluation logic inside this service initially.

Do NOT create a complicated evaluator factory or strategy pattern for this learning project.

A simple:

```java
if (question.getQuestionType() == QuestionType.SINGLE_CHOICE) {
    ...
} else if (question.getQuestionType() == QuestionType.ONE_LINE) {
    ...
} else if (question.getQuestionType() == QuestionType.TRUE_FALSE) {
    ...
}
```

is acceptable and preferred for this learning application.

---

# 24. Sample Data

Create a simple `data.sql` or startup initializer with:

### Admin

```text
Admin
admin@gmail.com
ADMIN
```

### User

```text
John
john@gmail.com
USER
```

### Quiz

```text
Java Basics
Basic Java programming
Programming
```

Add at least:

```text
2 SINGLE_CHOICE questions
1 ONE_LINE question
1 TRUE_FALSE question
```

This allows the complete submission flow to be tested immediately.

---

# 25. README

Create a beginner-friendly README containing:

```text
1. Project Overview
2. Technologies Used
3. Database Setup
4. How to Run
5. Database Tables
6. API Endpoints
7. Sample Requests
8. Sample Responses
9. Quiz Submission Flow
```

Also include the MySQL command:

```sql
CREATE DATABASE quiz_app;
```

---

# 26. Expected Final Result

The completed backend should support this complete flow:

```text
Admin
  ↓
Create Quiz
  ↓
Add Questions
  ↓
Add Correct Answers
  ↓
User opens Quiz
  ↓
User sees Questions
  ↓
User submits Answers
  ↓
Backend evaluates Answers
  ↓
Backend calculates Score
  ↓
QuizAttempt is saved
  ↓
User receives Score
```

Example:

```text
Java Basics

Total Questions: 5
Total Marks: 6

User Score:

5 / 6

Correct: 4
Wrong: 1
```

---

# 27. Code Generation Instructions

Generate the project step-by-step.

First generate:

```text
1. pom.xml
2. application.properties
3. Entity classes
4. Enums
5. Repository interfaces
6. DTOs
7. Services
8. Controllers
9. Exception handling
10. Sample data
11. README
```

For every important class, briefly explain:

* Why the class exists
* What it does
* How it connects to other classes

Make the code compile and run with **Java 17**.

Do not leave placeholder methods such as:

```java
// TODO implement
```

All required functionality must be implemented.

After generating the code, provide:

```text
Project Structure
Database Schema
API List
How to Run
Sample API Flow
```

The final implementation should be **simple enough for a beginner to understand but complete enough to demonstrate a real Spring Boot + MySQL application**.
