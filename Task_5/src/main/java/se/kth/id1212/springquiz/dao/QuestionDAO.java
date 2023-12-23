package se.kth.id1212.springquiz.dao;

import se.kth.id1212.springquiz.model.Question;

public interface QuestionDAO< Questions > {

    Question getQuestion(Integer questionID);

    Questions[] getAllQuestions(Integer quizID);
}
