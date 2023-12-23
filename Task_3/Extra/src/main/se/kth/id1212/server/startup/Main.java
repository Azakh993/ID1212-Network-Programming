package main.se.kth.id1212.server.startup;

import main.se.kth.id1212.server.controller.Controller;
import main.se.kth.id1212.server.model.GameSession;
import main.se.kth.id1212.server.util.ExceptionLogger;
import main.se.kth.id1212.server.view.View;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.io.FileInputStream;

import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Starts the server and listens for incoming connections.
 */
public class Main {
    private static final int PORT = 7847;
    private static final ConcurrentHashMap<String, GameSession> game_sessions = new ConcurrentHashMap<>();

    /**
     * Starts the server and listens for incoming connections. Once a connection is established, a Callable
     * object is created and passed to the View class which is then started in a new thread.
     * @param args No command line arguments are used.
     */
    public static void main(String[] args) {

        try {
            // Load the KeyStore file
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("keystore.jks");
            ks.load(fis, "password".toCharArray());

            // Initialize a KeyManagerFactory with the KeyStore
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, "password".toCharArray());

            // Initialize an SSLContext with the KeyManagerFactory
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(kmf.getKeyManagers(), null, null);

            // Create an SSLServerSocketFactory from the SSLContext
            SSLServerSocketFactory ssf = sc.getServerSocketFactory();

            // Create an SSLServerSocket from the SSLServerSocketFactory
            SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(PORT);

            while (true) {
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