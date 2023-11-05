package se.kth.id1212.model;

import java.util.Optional;

public interface UserDAO {
    Optional<User> getUser(String username, String password);
}
