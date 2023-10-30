package main.se.kth.id1212.server.startup;

import main.se.kth.id1212.server.controller.Controller;
import main.se.kth.id1212.server.model.GameSession;
import main.se.kth.id1212.server.util.ExceptionLogger;
import main.se.kth.id1212.server.view.View;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Starts the server and listens for incoming connections.
 */
public class Main {
    private static final int PORT = 7847;
    private static final HashMap<String, GameSession> game_sessions = new HashMap<>();

    /**
     * Starts the server and listens for incoming connections. Once a connection is established, a Callable
     * object is created and passed to the View class which is then started in a new thread.
     * @param args No command line arguments are used.
     */
    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                Callable<Object> controller = new Controller(game_sessions, clientSocket);
                View view = new View(controller, clientSocket);
                new Thread(view).start();
            }

        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }
}