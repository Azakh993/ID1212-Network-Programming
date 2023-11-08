package se.kth.id1212.integration;

import se.kth.id1212.model.User;
import se.kth.id1212.model.UserDAO;
import se.kth.id1212.util.ExceptionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {
    private final Connection connection = DatabaseHandler.connect();

    @Override
    public User getUser(String username, String password) {
        User user = null;
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?");
                statement.setString(1, username);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    user = new User(resultSet.getInt("ID"), resultSet.getString("USERNAME"), resultSet.getString("PASSWORD"));
                }
            } catch (SQLException exception) {
                ExceptionLogger.log(exception);
            }
        }
        return user;
    }
}