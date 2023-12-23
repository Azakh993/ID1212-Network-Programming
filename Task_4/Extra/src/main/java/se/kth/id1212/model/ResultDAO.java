package se.kth.id1212.model;

import java.util.HashMap;

/**
 * An interface that provides methods to retrieve all results for a specific user
 * and to add a new result for a user's performance in a quiz.
 *
 * @param <Result> The type representing a quiz result.
 */
public interface ResultDAO<Result> {

    /**
     * Retrieves all results for a specific user based on their unique identifier.
     *
     * @param userID The unique identifier of the user to retrieve results for.
     * @return A HashMap with quiz IDs as keys and corresponding Result objects as values.
     */
    HashMap<Integer, Result> getAllResults(Integer userID);

    /**
     * Adds a new result for a user's performance in a quiz.
     *
     * @param result The Result object to add.
     */
    void addResult(Result result);
}
