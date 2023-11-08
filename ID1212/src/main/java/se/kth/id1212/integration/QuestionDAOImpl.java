package se.kth.id1212.integration;

import se.kth.id1212.model.Question;
import se.kth.id1212.model.QuestionDAO;
import se.kth.id1212.util.ExceptionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAOImpl implements QuestionDAO< Question > {
    private final Connection connection = DatabaseHandler.connect();

    @Override
    public Question getQuestion(Integer questionID) {
        try {
            String query = "SELECT * FROM QUESTIONS WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, questionID);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String text = resultSet.getString("TEXT");
                String options = resultSet.getString("OPTIONS");
                String answer = resultSet.getString("ANSWER");

                return new Question(questionID, text, answer, options.split(","));
            }
        } catch (SQLException exception) {
            ExceptionLogger.log(exception);
        }
        return null;
    }

    @Override
    public Question[] getAllQuestions(Integer quizID) {
        List< Question > questions = new ArrayList<>();
        try {
            String query = "SELECT Q.ID, Q.TEXT, Q.OPTIONS, Q.ANSWER FROM QUESTIONS Q " +
                    "INNER JOIN SELECTOR S ON Q.ID = S.QUESTION_ID " +
                    "WHERE S.QUIZ_ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, quizID);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int questionID = resultSet.getInt("ID");
                String text = resultSet.getString("TEXT");
                String options = resultSet.getString("OPTIONS");
                String answer = resultSet.getString("ANSWER");

                Question question = new Question(questionID, text, answer, options.split(","));
                questions.add(question);
            }
        } catch (SQLException exception) {
            ExceptionLogger.log(exception);
        }
        return questions.toArray(new Question[0]);
    }
}
