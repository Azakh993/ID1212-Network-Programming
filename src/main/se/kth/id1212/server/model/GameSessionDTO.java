package main.se.kth.id1212.server.model;

/**
 * A data transfer object for a game session.
 */
public record GameSessionDTO(String session_id, int number_of_guesses, String guess_outcome, boolean new_user) {
}
