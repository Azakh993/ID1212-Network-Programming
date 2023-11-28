package se.kth.id1212.springquiz.dao;

public interface QuizDAO< Quiz > {

    Quiz getQuiz(Integer id);

    Quiz[] getAllQuizzes();
}
