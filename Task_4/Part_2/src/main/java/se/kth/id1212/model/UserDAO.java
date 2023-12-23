package se.kth.id1212.model;

/**
 * An interface that provides a method to retrieve a user based on their
 * username and password for authentication purposes.
 */
public interface UserDAO {

    /**
     * Retrieves a user based on the provided username and password.
     *
     * @param username The username (email) used for authentication.
     * @param password The password associated with the user's account.
     * @return The User object associated with the provided username and password.
     */
    User getUser(String username, String password);
}
