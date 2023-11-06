package se.kth.id1212.model;

import java.util.HashMap;

public interface ResultDAO< Result > {
    HashMap< Integer, Result > getAllResults(String username);

    void updateResult(Integer userId, Integer quizId);

    void addResult(Integer userId, Integer quizId);
}
