package main.se.kth.id1212.server.model;

/**
 * Represents a game session.
 */
public class GameSession {
    private final String session_id;
    private int number_of_guesses;
    private int last_guess;
    private int secret_number;
    private String guess_outcome;
    private boolean new_user;

    /**
     * Creates a new instance of a game session.
     * @param session_id    The session id of the game session, which is the same as the browser cookie.
     */
    public GameSession(String session_id) {
        this.session_id = session_id;
        this.number_of_guesses = 0;
        this.guess_outcome = "";
        this.secret_number = generate_random_number();
        this.new_user = true;
    }

    /**
     * Evaluates the user's guess and updates the game session accordingly. If the user has already made the same guess
     * as the one provided, nothing happens. If the user has made a new guess, the number of guesses is incremented and
     * the guess outcome is updated.
     */
    public void guess(int guess) {
        if (guess == last_guess) {
            return;
        }

        if (this.new_user) {
            this.new_user = false;
        }

        this.last_guess = guess;
        if (guess == secret_number) {
            this.guess_outcome = "CORRECT";
        } else if (guess < secret_number) {
            this.guess_outcome = "LOW";
        } else {
            this.guess_outcome = "HIGH";
        }

        increment_number_of_guesses();
    }

    /**
     * Restarts the game session by generating a new secret number and resetting the number of guesses and the guess
     * outcome.
     */
    public void restart_game() {
        this.number_of_guesses = 0;
        this.secret_number = generate_random_number();
        this.guess_outcome = "";
    }

    /**
     * Generates a random number between 1 and 100.
     * @return  A random number between 1 and 100.
     */
    private int generate_random_number() {
        return (int) (Math.random() * 100) + 1;
    }

    /**
     * Increments the number of guesses by 1.
     */
    private void increment_number_of_guesses() {
        this.number_of_guesses += 1;
    }

    /**
     * Sets the guess outcome to the provided one.
     * @param guess_outcome The number of guesses.
     */
    public void setGuess_outcome(String guess_outcome) {
        this.guess_outcome = guess_outcome;
    }

    /**
     * Returns a DTO representation of the game session.
     * @return  A DTO representation of the game session.
     */
    public GameSessionDTO generateGameSessionDTO() {
        return new GameSessionDTO(this.session_id, this.number_of_guesses, this.guess_outcome, this.new_user);
    }
}
