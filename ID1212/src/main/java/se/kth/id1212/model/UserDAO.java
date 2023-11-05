package se.kth.id1212.model;

public interface UserDAO {
    User getUser(String username, String password);
}
