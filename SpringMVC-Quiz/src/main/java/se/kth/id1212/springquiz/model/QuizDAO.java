package se.kth.id1212.springquiz.model;

public interface QuizDAO< Quiz > {

    Quiz getQuiz(Integer id);

    Quiz[] getAllQuizzes();
}
