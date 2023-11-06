package se.kth.id1212.integration;

import se.kth.id1212.model.Quiz;
import se.kth.id1212.model.QuizDAO;

public class QuizDAOImpl implements QuizDAO<Quiz> {
    @Override
    public Quiz getQuiz(Integer quizID) {
        return new Quiz(1, "Test 1");
    }

    @Override
    public Quiz[] getAllQuizzes() {
        Quiz[] quizzes = new Quiz[4];
        quizzes[0] = new Quiz(1, "Test 1");
        quizzes[1] = new Quiz(2, "Test 2");
        quizzes[2] = new Quiz(3, "Test 3");
        quizzes[3] = new Quiz(4, "Test 4");
        return quizzes;
    }
}
