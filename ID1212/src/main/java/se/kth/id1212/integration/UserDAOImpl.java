package se.kth.id1212.integration;

import se.kth.id1212.model.User;
import se.kth.id1212.model.UserDAO;

import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    @Override
    public Optional<User> getUser(String username, String password) {
        return Optional.empty();
    }
}
