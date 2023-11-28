package se.kth.id1212.springquiz.model;

public interface UserDAO {

    User getUser(String username, String password);
}
