package se.kth.id1212.model;

import java.util.HashMap;

public interface ResultDAO< Result > {
    HashMap< Integer, Result > getAllResults(String username);

    void addResult(Integer userId, Integer quizId, Integer points);
}
