package main.se.kth.id1212.server.view;

import main.se.kth.id1212.server.model.GameSessionDTO;
import main.se.kth.id1212.server.util.ExceptionLogger;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Outputs the current state of the game to the client.
 */
public class View implements Runnable {
    private final Socket clientSocket;
    private final Callable<Object> controller;
    private final PrintWriter output_to_client;

    /**
     * Creates a new instance of the view.
     * Instantiates the output stream to the client.
     * @param controller The controller that is used to get the current state of the game.
     * @param socket     The socket that is used to communicate with the client.
     */
    public View(Callable<Object> controller, Socket socket) {
        this.controller = controller;
        this.clientSocket = socket;
        try {
            this.output_to_client = new PrintWriter(clientSocket.getOutputStream(), false);
        } catch (Exception exception) {
            throw new RuntimeException("Could not create view.", exception);
        }
    }

    /**
     * Starts a new thread that runs the controller, which is a callable object. The controller is then executed.
     * The result is then used to update the view presented to the client.
     */
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

    /**
     * Generates an HTTP response based on the current state of the game. The response is then sent to the client.
     * @param current_session_state The current state of the game.
     */
    private void updateView(GameSessionDTO current_session_state) {
        String response = generate_HTTP_response(current_session_state);
        output_to_client.write(response);
        output_to_client.flush();
        close_connection();
    }

    /**
     * Generates an HTTP response based on whether the user is new or not, and the current state of the game.
     * @param current_session_state The current state of the game.
     * @return The HTTP response.
     */
    private String generate_HTTP_response(GameSessionDTO current_session_state) {
        int number_of_guesses = current_session_state.number_of_guesses();
        String guess_outcome = current_session_state.guess_outcome();
        String session_id = current_session_state.session_id();
        boolean new_user = current_session_state.new_user();

        String header;
        if (new_user) {
            header = StringResourcesView.generate_HTTP_header_with_cookie(session_id);
        } else {
            header = StringResourcesView.generate_HTTP_header();
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

    /**
     * Formats the guess outcome to a more user-friendly format.
     * @param guess_outcome The guess outcome to be formatted.
     * @return The formatted guess outcome.
     */
    private String format_guess_outcome(String guess_outcome) {
        return switch (guess_outcome) {
            case "CORRECT" -> "Correctly guessed. Good job!";
            case "LOW" -> "Your guess was too low.";
            case "HIGH" -> "Your guess was too high.";
            case "INVALID" -> "Invalid guess. Try again!";
            default -> "";
        };
    }

    /**
     * Closes the connection to the client.
     */
    private void close_connection() {
        try {
            this.clientSocket.close();
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }
}
