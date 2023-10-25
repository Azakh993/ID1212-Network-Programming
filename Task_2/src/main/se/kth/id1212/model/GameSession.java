package main.se.kth.id1212.model;

public class GameSession {
    private final String session_id;
    private int number_of_guesses;
    private int last_guess;
    private int secret_number;
    private String guess_outcome;
    private boolean new_user;

    public GameSession(String session_id) {
        this.session_id = session_id;
        this.number_of_guesses = 0;
        this.guess_outcome = "";
        this.secret_number = generate_random_number();
        this.new_user = true;
    }

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

    public void restart_game() {
        this.number_of_guesses = 0;
        this.secret_number = generate_random_number();
        this.guess_outcome = "";
    }

    private int generate_random_number() {
        return (int) (Math.random() * 100) + 1;
    }

    private void increment_number_of_guesses() {
        this.number_of_guesses += 1;
    }

    public void setGuess_outcome(String guess_outcome) {
        this.guess_outcome = guess_outcome;
    }

    public GameSessionDTO generateGameSessionDTO() {
        return new GameSessionDTO(this.session_id, this.number_of_guesses, this.guess_outcome, this.new_user);
    }
}
