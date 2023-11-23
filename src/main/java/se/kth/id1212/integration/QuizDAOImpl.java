package se.kth.id1212.integration;

import se.kth.id1212.model.Quiz;
import se.kth.id1212.model.QuizDAO;
import se.kth.id1212.util.ExceptionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to retrieve individual quizzes
 * and all quizzes from the database.
 */
public class QuizDAOImpl implements QuizDAO< Quiz > {
    private final Connection connection = DatabaseHandler.connect();

    /**
     * Retrieves a specific quiz based on its unique identifier from the database.
     *
     * @param quizID The unique identifier of the quiz to retrieve.
     * @return The Quiz object associated with the given identifier, or null if not found.
     */
    @Override
    public Quiz getQuiz(Integer quizID) {
        Quiz quiz = null;
        String sql = "SELECT * FROM QUIZZES WHERE ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quizID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String subject = resultSet.getString("SUBJECT");
                    quiz = new Quiz(id, subject);
                }
            }
        } catch (SQLException exception) {
            ExceptionLogger.log(exception);
        }

        return quiz;
    }

    /**
     * Retrieves all quizzes from the database.
     *
     * @return An array of Quiz objects representing all quizzes in the database.
     */
    @Override
    public Quiz[] getAllQuizzes() {
        List< Quiz > quizzes = new ArrayList<>();
        String sql = "SELECT * FROM QUIZZES";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String subject = resultSet.getString("SUBJECT");
                Quiz quiz = new Quiz(id, subject);
                quizzes.add(quiz);
            }
        } catch (SQLException exception) {
            ExceptionLogger.log(exception);
        }

        return quizzes.toArray(new Quiz[0]);
    }
}