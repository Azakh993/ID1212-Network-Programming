package se.kth.id1212.springquiz.dao;

public interface UserDAO< User > {

    User getUserByUsername(String username);

    User getUserByUserID(String userID);
}