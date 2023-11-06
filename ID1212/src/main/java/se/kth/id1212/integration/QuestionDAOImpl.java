package se.kth.id1212.integration;

import se.kth.id1212.model.Question;
import se.kth.id1212.model.QuestionDAO;

public class QuestionDAOImpl implements QuestionDAO< Question > {
    @Override
    public Question getQuestion(Integer questionID) {
        return new Question(1, "What is the capital of Sweden?", "Stockholm", new String[]{"Stockholm", "Gothenburg", "Malmö", "Uppsala"});
    }

    @Override
    public Question[] getAllQuestions(Integer quizID) {
        Question[] questions = new Question[4];
        questions[0] = new Question(1, "What is the capital of Sweden?", "Stockholm", new String[]{"Stockholm", "Gothenburg", "Malmö", "Uppsala"});
        questions[1] = new Question(2, "What is the capital of France?", "Paris", new String[]{"Paris", "Lyon", "Marseille", "Toulouse"});
        questions[2] = new Question(3, "What is the capital of Germany?", "Berlin", new String[]{"Berlin", "Hamburg", "Munich", "Cologne"});
        questions[3] = new Question(4, "What is the capital of Italy?", "Rome", new String[]{"Rome", "Milan", "Naples", "Turin"});
        return questions;
    }
}
