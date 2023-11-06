package se.kth.id1212.integration;

import se.kth.id1212.model.Result;
import se.kth.id1212.model.ResultDAO;

import java.util.HashMap;

public class ResultDAOImpl implements ResultDAO<Result> {

    @Override
    public HashMap<Integer, Result> getAllResults(String userID) {
        HashMap<Integer, Result> results = new HashMap<>();
        results.put(1, new Result(1, 1, 10));
        results.put(2, new Result(1, 2, 9));
        results.put(4, new Result(1, 4, 3));
        return results;
    }

    @Override
    public void updateResult(Integer userID, Integer quizId) {

    }

    @Override
    public void addResult(Integer userID, Integer quizId) {

    }
}
