package se.kth.id1212.springquiz.dao;

import se.kth.id1212.springquiz.model.User;

public interface UserDAO {

    User getUser(String username, String password);
}
