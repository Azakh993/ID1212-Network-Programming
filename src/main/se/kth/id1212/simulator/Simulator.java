package main.se.kth.id1212.simulator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * This class is responsible for simulating 100 game sessions of the guessing game.
 * The average number of guesses is calculated and printed to the console.
 */
public class Simulator {

    private static FutureTask[] gameSession;

    /**
     * This method creates 100 game sessions and calculates the average number of guesses. First, a
     * FutureTask array is created to store the game sessions. Then, a for-loop is used to create
     * the game sessions and start them. The game sessions are started in batches of 25 to avoid
     * overloading the server. Finally, the average number of guesses is calculated and printed to
     * the console.
     * @param args     The command line arguments, not used.
     */
    public static void main(String[] args) {
        FutureTask[] game_session = new FutureTask[100];

        for(int i = 0; i < 100; i++) {
            Callable<Object> simulatorThread = new SimulatorThread();
            game_session[i] = new FutureTask<>(simulatorThread);
            new Thread(game_session[i]).start();

            if(i % 25 == 0) {
                while(!game_session[i].isDone());
            }
        }

        System.out.println("Average number of guesses: " + average_number_of_guesses(game_session));
    }

    /**
     * This method calculates the average number of guesses. First, the number of guesses for each
     * game session is extracted from the FutureTask array. Then, the average number of guesses is
     * calculated and returned.
     * @param game_session      The FutureTask array containing the game sessions.
     * @return                  The average number of guesses.
     */
    private static int average_number_of_guesses(FutureTask[] game_session) {
        gameSession = game_session;
        int total_number_of_guesses = 0;
        for(int i = 0; i < 100; i++) {
            try {
                total_number_of_guesses += (int) game_session[i].get();
            } catch (Exception exception) {
                ExceptionLogger.logExceptionToFile(exception);
            }
        }
        return total_number_of_guesses / 100;
    }
}

/**
 * This class is responsible for simulating a game session of the guessing game.
 */
class SimulatorThread implements Callable<Object> {
    private HttpURLConnection connection;
    private String cookie;
    private int current_guess;
    private int guesses_until_correct = 0;
    private boolean correct_guess = false;
    private int lower_bound = 1;
    private int upper_bound = 100;

    /**
     * This method is responsible for simulating a game session of the guessing game. First, a
     * connection to the server is established. Then, a while-loop is used to generate new guesses
     * until the correct guess is found. The outcome of the guess is extracted from the server
     * response and the guess parameters are adjusted accordingly.
     * @return      The number of guesses until the correct guess was found.
     */
    @Override
    public Object call() {
        start_game_session();

        while(!correct_guess) {
            generate_new_guess();
            send_guess();
            String outcome = extract_outcome_from_response();
            adjust_guess_parameters(outcome);
        }

        return this.guesses_until_correct;
    }

    /**
     * This method is responsible for starting a game session. First, a connection to the server is
     * established. Then, the cookie is extracted from the server response.
     */
    private void start_game_session() {
        try {
            URL url = new URI("http://localhost:7847/").toURL();
            this.connection = (HttpURLConnection) url.openConnection();
            this.connection.setRequestMethod("GET");
            this.connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            this.cookie = connection.getHeaderField("Set-Cookie");
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }

    /**
     * This method is responsible for generating a new guess. First, the number of guesses until the
     * correct guess is incremented. Then, a new guess is generated, using the lower and upper bound
     * as parameters.
     */
    private void generate_new_guess() {
        this.guesses_until_correct++;
        this.current_guess = (int) (Math.random() * ((upper_bound - lower_bound) + 1)) + lower_bound;

    }

    /**
     * This method is responsible for sending the guess to the server. First, a connection to the
     * server is established. Then, the guess is sent to the server.
     */
    private void send_guess() {
        try {
            URL url = new URI("http://localhost:7847/?guess=" + this.current_guess).toURL();
            this.connection = (HttpURLConnection) url.openConnection();
            this.connection.setRequestMethod("GET");
            this.connection.setRequestProperty("Cookie", this.cookie);
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }

    /**
     * This method is responsible for extracting the outcome of the guess from the server response.
     * First, the server response is read line by line. Then, the outcome is extracted from the
     * server response and returned.
     * @return      The outcome of the guess.
     */
    private String extract_outcome_from_response() {
        try {
            BufferedReader input_from_server = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            String received_request_line;

            while ((received_request_line = input_from_server.readLine()) != null) {
                if (received_request_line.isEmpty()) {
                    break;
                } else if (received_request_line.contains("Correct")) {
                    return "correct";
                } else if (received_request_line.contains("low")) {
                    return "low";
                } else if (received_request_line.contains("high")) {
                    return "high";
                }
            }
            input_from_server.close();
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
        return "invalid";
    }

    /**
     * This method is responsible for adjusting the guess parameters; the outcome of the
     * guess is used to determine which guess parameter to adjust.
     * @param outcome       The outcome of the guess.
     */
    private void adjust_guess_parameters(String outcome) {
        switch (outcome) {
            case "low" -> this.lower_bound = this.current_guess;
            case "high" -> this.upper_bound = this.current_guess;
            case "correct" -> this.correct_guess = true;
            default -> ExceptionLogger.logExceptionToFile(new Exception("Invalid outcome: " + outcome));
        }
    }
}
