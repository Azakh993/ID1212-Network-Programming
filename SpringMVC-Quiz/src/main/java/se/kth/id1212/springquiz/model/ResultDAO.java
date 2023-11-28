package se.kth.id1212.springquiz.model;

import java.util.HashMap;

public interface ResultDAO<Result> {

    HashMap<Integer, Result> getAllResults(Integer userID);

    void addResult(Integer userID, Integer quizID, Integer score);
}
