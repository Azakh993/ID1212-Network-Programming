package se.kth.id1212.integration;

import se.kth.id1212.model.User;
import se.kth.id1212.model.UserDAO;

public class UserDAOImpl implements UserDAO {
    @Override
    public User getUser(String username, String password) {
        return new User(1, "test", "test");
    }
}
