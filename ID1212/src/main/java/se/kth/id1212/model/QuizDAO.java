package se.kth.id1212.model;

public interface QuizDAO<Quiz> {
    Quiz getQuiz(String subject);
    Quiz[] getAllQuizzes();
}
