package se.kth.id1212.integration;

import se.kth.id1212.model.User;
import se.kth.id1212.model.UserDAO;
import se.kth.id1212.util.ExceptionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class provides a method to retrieve a user from the database
 * based on a given username and password.
 */
public class UserDAOImpl implements UserDAO {
    private final Connection connection = DatabaseHandler.connect();

    /**
     * Retrieves a user from the database based on the provided username and password.
     *
     * @param username The username of the user to retrieve.
     * @param password The password associated with the username.
     * @return The User object associated with the given username and password, or null if not found.
     */
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