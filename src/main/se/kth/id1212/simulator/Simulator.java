package main.se.kth.id1212.simulator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Simulator {
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

    private static int average_number_of_guesses(FutureTask[] game_session) {
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

class SimulatorThread implements Callable<Object> {
    private HttpURLConnection connection;
    private String cookie;
    private int current_guess;
    private int guesses_until_correct = 0;
    private boolean correct_guess = false;
    private int lower_bound = 1;
    private int upper_bound = 100;

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

    private void generate_new_guess() {
        this.guesses_until_correct++;
        this.current_guess = (int) (Math.random() * (upper_bound - lower_bound)) + lower_bound;

    }

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

    private void adjust_guess_parameters(String outcome) {
        switch (outcome) {
            case "low" -> this.lower_bound = this.current_guess;
            case "high" -> this.upper_bound = this.current_guess;
            case "correct" -> this.correct_guess = true;
            default -> ExceptionLogger.logExceptionToFile(new Exception("Invalid outcome: " + outcome));
        }
    }
}
