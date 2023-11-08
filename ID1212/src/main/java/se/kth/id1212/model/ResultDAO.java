package se.kth.id1212.model;

import java.util.HashMap;

public interface ResultDAO< Result > {
    HashMap< Integer, Result > getAllResults(Integer userID);

    void addResult(Integer userId, Integer quizId, Integer points);
}
