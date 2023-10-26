package main.se.kth.id1212.server.startup;

import main.se.kth.id1212.server.controller.Controller;
import main.se.kth.id1212.server.model.GameSession;
import main.se.kth.id1212.server.util.ExceptionLogger;
import main.se.kth.id1212.server.view.View;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class Main {
    private static final int PORT = 7847;
    private static final HashMap<String, GameSession> game_sessions = new HashMap<>();

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