package main.se.kth.id1212.controller;

import main.se.kth.id1212.model.GameSession;
import main.se.kth.id1212.util.ExceptionLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

public class Controller implements Callable<Object> {
    private final Socket clientSocket;
    private final BufferedReader input_from_client;
    private final HashMap<String, GameSession> gameSessions;
    private GameSession current_game_session;

    public Controller(HashMap<String, GameSession> gameSessions, Socket clientSocket) {
        this.gameSessions = gameSessions;
        this.clientSocket = clientSocket;
        try {
            this.input_from_client = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception exception) {
            throw new RuntimeException("Could not create controller.", exception);
        }
    }

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

    private ArrayList<String> store_request() {
        ArrayList<String> http_request = new ArrayList<>();
        String received_request_line;
        try {
            if(this.input_from_client != null) {
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

    private boolean request_for_favorite_icon(ArrayList<String> http_request) {
        if(!http_request.isEmpty()) {
            for (String line : http_request) {
                if (line.contains("favicon.ico")) {
                    return true;
                }
            }
        }
        return false;
    }

    private String get_cookie(ArrayList<String> http_request) {
        if(!http_request.isEmpty()) {
            for (String line : http_request) {
                if (line.contains("Cookie")) {
                    return line.split("=")[1];
                }
            }
        }
        return UUID.randomUUID().toString();
    }

    private void process_request(ArrayList<String> http_request) {
        if(http_request == null || http_request.isEmpty()) {
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

    private void start_new_game(String cookie) {
        this.current_game_session =  new GameSession(cookie);
        this.gameSessions.put(cookie, this.current_game_session);
    }

    private void get_existing_session(String cookie) {
        this.current_game_session = this.gameSessions.get(cookie);
    }

    private void close_connection() {
        try {
            this.clientSocket.close();
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }
}
