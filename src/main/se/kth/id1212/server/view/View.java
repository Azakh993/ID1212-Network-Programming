package main.se.kth.id1212.server.view;

import main.se.kth.id1212.server.model.GameSessionDTO;
import main.se.kth.id1212.server.util.ExceptionLogger;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class View implements Runnable {
    private final Socket clientSocket;
    private final Callable<Object> controller;
    private final PrintWriter output_to_client;


    public View(Callable<Object> controller, Socket socket) {
        this.controller = controller;
        this.clientSocket = socket;
        try {
            this.output_to_client = new PrintWriter(clientSocket.getOutputStream(), false);
        } catch (Exception exception) {
            throw new RuntimeException("Could not create view.", exception);
        }
    }

    @Override
    public void run() {
        FutureTask<Object> game_session = new FutureTask<>(this.controller);
        new Thread(game_session).start();
        try {
            updateView((GameSessionDTO) game_session.get());
        } catch (InterruptedException | ExecutionException e) {
            ExceptionLogger.logExceptionToFile(e);
        }
    }

    private void updateView(GameSessionDTO current_session_state) {
        String response = generate_HTTP_response(current_session_state);
        output_to_client.write(response);
        output_to_client.flush();
        close_connection();
    }

    private String generate_HTTP_response(GameSessionDTO current_session_state) {
        int number_of_guesses = current_session_state.number_of_guesses();
        String guess_outcome = current_session_state.guess_outcome();
        String session_id = current_session_state.session_id();
        boolean new_user = current_session_state.new_user();

        String header;
        if (new_user) {
            header = StringResourcesView.generate_HTTP_header_with_cookie(session_id);
        } else {
            header = StringResourcesView.generate_HTTP_header(session_id);
        }

        String html;
        if (guess_outcome.equals("CORRECT")) {
            guess_outcome = format_guess_outcome(guess_outcome);
            html = StringResourcesView.generateRestartHTML(number_of_guesses, guess_outcome);
        } else {
            guess_outcome = format_guess_outcome(guess_outcome);
            html = StringResourcesView.generateDefaultHTML(number_of_guesses, guess_outcome);
        }
        return header + html;
    }

    private String format_guess_outcome(String guess_outcome) {
        return switch (guess_outcome) {
            case "CORRECT" -> "Correctly guessed. Good job!";
            case "LOW" -> "Your guess was too low.";
            case "HIGH" -> "Your guess was too high.";
            case "INVALID" -> "Invalid guess. Try again!";
            default -> "";
        };
    }

    private void close_connection() {
        try {
            this.clientSocket.close();
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }
}
