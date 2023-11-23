package se.kth.id1212.model;

/**
 * Represents a game session.
 */
public class GameSession {
    private int number_of_guesses;
    private int last_guess;
    private int secret_number;
    private String guess_outcome;

    /**
     * Creates a new instance of a game session.
     */
    public GameSession() {
        this.number_of_guesses = 0;
        this.guess_outcome = "";
        this.secret_number = generate_random_number();
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
    public void restartGame() {
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
     * Returns the latest guess outcome
     * @return The latest guess outcome.
     */
    public String getGuessOutcome() {
        return guess_outcome;
    }

    /**
     * Returns the number of guesses.
     * @return  The number of guesses.
     */
    public int getNumberOfGuesses() {
        return number_of_guesses;
    }

    /**
     * Sets the guess outcome.
     * @param guess_outcome The guess outcome.
     */
    public void setGuessOutcome(String guess_outcome) {
        this.guess_outcome = guess_outcome;
    }
}
