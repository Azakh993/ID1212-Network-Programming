package main.se.kth.id1212.server.controller;

import main.se.kth.id1212.server.model.GameSession;
import main.se.kth.id1212.server.util.ExceptionLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the communication between the server and a client.
 */
public class Controller implements Callable<Object> {
    private final Socket clientSocket;
    private final BufferedReader input_from_client;
    private final ConcurrentHashMap<String, GameSession> gameSessions;
    private GameSession current_game_session;

    /**
     * Creates a new instance of a client handler (controller).
     * @param gameSessions          A list of all game sessions.
     * @param clientSocket          The socket of the client.
     */
    public Controller(ConcurrentHashMap<String, GameSession> gameSessions, Socket clientSocket) {
        this.gameSessions = gameSessions;
        this.clientSocket = clientSocket;
        try {
            this.input_from_client = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception exception) {
            throw new RuntimeException("Could not create controller.", exception);
        }
    }

    /**
     * Handles a client request. If the request is for the favicon, the connection is closed. If the request is not for
     * favicon, the request is checked for a cookie. If a cookie is found, the corresponding game session is retrieved,
     * and the request is processed. If no cookie is found, a new game session is started. Finally, a DTO of the game
     * session is returned.
     * @return     A DTO of the game session.
     */
    @Override
    public Object call() {
        try {
            ArrayList<String> http_request = store_request();

            if (request_for_favorite_icon(http_request)) {
                close_connection();
            }

            String session_id = get_cookie(http_request);

            if (gameSessions.containsKey(session_id)) {
                get_existing_session(session_id);
                process_request(http_request);
            } else {
                start_new_game(session_id);
            }

        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }

        return this.current_game_session.generateGameSessionDTO();
    }

    /**
     * Stores the client request in an ArrayList. The request is stored line by line until an empty line is found.
     * @return      The client request.
     */
    private ArrayList<String> store_request() {
        ArrayList<String> http_request = new ArrayList<>();
        String received_request_line;
        try {
            if (this.input_from_client != null) {
                while ((received_request_line = this.input_from_client.readLine()) != null) {
                    http_request.add(received_request_line);
                    if (received_request_line.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
        return http_request;
    }

    /**
     * Checks if the client request is for the favicon.
     * @param http_request      The client request.
     * @return                  True if the request is for the favicon, false otherwise.
     */
    private boolean request_for_favorite_icon(ArrayList<String> http_request) {
        if (!http_request.isEmpty()) {
            for (String line : http_request) {
                if (line.contains("favicon.ico")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves the cookie from the client request. If no cookie is found, a new cookie is generated.
     * @param http_request      The client request.
     * @return                  The cookie, which is either retrieved from the client request or generated.
     */
    private String get_cookie(ArrayList<String> http_request) {
        if (!http_request.isEmpty()) {
            for (String line : http_request) {
                if (line.contains("Cookie")) {
                    return line.split("=")[1];
                }
            }
        }
        return UUID.randomUUID().toString();
    }

    /**
     * Processes the client request. If the request contains the string "restart=true", the game is restarted. If the
     * request contains the string "guess", the guess is retrieved from the request and the game session is updated.
     * @param http_request      The client request.
     */
    private void process_request(ArrayList<String> http_request) {
        if (http_request == null || http_request.isEmpty()) {
            return;
        }

        for (String line : http_request) {
            if (line.contains("restart=true")) {
                this.current_game_session.restart_game();
                break;
            } else if (line.contains("guess")) {
                String[] parts = line.split("=");
                try {
                    int guess = Integer.parseInt(parts[1].split(" ")[0]);
                    this.current_game_session.guess(guess);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                    this.current_game_session.setGuess_outcome("INVALID");
                }
                break;
            }
        }
    }

    /**
     * Starts a new game session.
     * @param cookie        The cookie of the new game session.
     */
    private void start_new_game(String cookie) {
        this.current_game_session = new GameSession(cookie);
        this.gameSessions.put(cookie, this.current_game_session);
    }

    /**
     * Retrieves the game session corresponding to the cookie.
     * @param cookie        The cookie of the game session.
     */
    private void get_existing_session(String cookie) {
        this.current_game_session = this.gameSessions.get(cookie);
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
