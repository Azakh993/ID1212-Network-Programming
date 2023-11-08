package se.kth.id1212.integration;

import se.kth.id1212.model.Result;
import se.kth.id1212.model.ResultDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ResultDAOImpl implements ResultDAO< Result > {
    private Connection connection = DatabaseHandler.connect();

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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public void addResult(Integer userId, Integer quizId, Integer points) {
        try {
            String checkQuery = "SELECT ID FROM RESULTS WHERE USER_ID = ? AND QUIZ_ID = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setInt(1, userId);
            checkStatement.setInt(2, quizId);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                int existingRecordId = resultSet.getInt("ID");
                String updateQuery = "UPDATE RESULTS SET SCORE = ? WHERE ID = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setInt(1, points);
                updateStatement.setInt(2, existingRecordId);
                updateStatement.executeUpdate();
            } else {
                String insertQuery = "INSERT INTO RESULTS (USER_ID, QUIZ_ID, SCORE) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setInt(1, userId);
                insertStatement.setInt(2, quizId);
                insertStatement.setInt(3, points);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
