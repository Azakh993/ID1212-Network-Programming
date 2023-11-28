package se.kth.id1212.springquiz.model;

public interface QuestionDAO< Questions > {

    Question getQuestion(Integer questionID);

    Questions[] getAllQuestions(Integer quizID);
}
