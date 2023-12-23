package se.kth.id1212.model;

/**
 * Represents a user with their associated details for authentication.
 *
 * @param id       The unique identifier for the user.
 * @param username The username used for authentication.
 * @param password The password associated with the user's account.
 */
public record User( Integer id, String username, String password ) {
}
