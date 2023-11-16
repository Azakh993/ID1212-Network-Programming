package se.kth.id1212.integration;

import se.kth.id1212.model.Result;
import se.kth.id1212.model.ResultDAO;
import se.kth.id1212.util.ExceptionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class provides methods to retrieve all quiz results for a user
 * and to add a new quiz result to the database.
 */
public class ResultDAOImpl implements ResultDAO< Result > {
    private final Connection connection = DatabaseHandler.connect();


    /**
     * Retrieves all quiz results for a specific user from the database.
     *
     * @param userID The unique identifier of the user for whom to retrieve quiz results.
     * @return A HashMap containing quiz IDs as keys and corresponding Result objects as values.
     */
    @Override
    public HashMap< Integer, Result > getAllResults(Integer userID) {
        HashMap< Integer, Result > results = new HashMap<>();
        String query = "SELECT * FROM RESULTS WHERE USER_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int quizId = resultSet.getInt("QUIZ_ID");
                int score = resultSet.getInt("SCORE");
                Result result = new Result(userID, quizId, score);
                results.put(quizId, result);
            }
        } catch (SQLException exception) {
            ExceptionLogger.log(exception);
        }

        return results;
    }

    /**
     * Adds or updates quiz results for a specific user in the database.
     *
     * @param result The Result object containing the quiz results to add or update.
     */
    @Override
    public void addResult(Result result) {
        Integer userId = result.userID();
        Integer quizId = result.quizID();
        Integer points = result.score();

        try {
            String checkQuery = "SELECT ID FROM RESULTS WHERE USER_ID = ? AND QUIZ_ID = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setInt(1, userId);
            checkStatement.setInt(2, quizId);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                updateResult(resultSet, points);
            } else {
                insertResult(userId, quizId, points);
            }
        } catch (SQLException exception) {
            ExceptionLogger.log(exception);
        }
    }

    /**
     * Updates an existing result in the database with the given points.
     *
     * @param resultSet The ResultSet containing the existing result's information.
     * @param points    The new score to update in the database.
     * @throws SQLException If a SQL exception occurs during the update process.
     */
    private void updateResult(ResultSet resultSet, Integer points) throws SQLException {
        int existingRecordId = resultSet.getInt("ID");
        String updateQuery = "UPDATE RESULTS SET SCORE = ? WHERE ID = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        updateStatement.setInt(1, points);
        updateStatement.setInt(2, existingRecordId);
        updateStatement.executeUpdate();
    }

    /**
     * Inserts a new result for a user and quiz with the given points into the database.
     *
     * @param userID The unique identifier of the user.
     * @param quizID The unique identifier of the quiz.
     * @param points The score achieved in the quiz.
     * @throws SQLException If a SQL exception occurs during the insertion process.
     */
    private void insertResult(Integer userID, Integer quizID, Integer points) throws SQLException {
        String insertQuery = "INSERT INTO RESULTS (USER_ID, QUIZ_ID, SCORE) VALUES (?, ?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.setInt(1, userID);
        insertStatement.setInt(2, quizID);
        insertStatement.setInt(3, points);
        insertStatement.executeUpdate();
    }
}
