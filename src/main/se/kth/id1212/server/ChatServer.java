package main.se.kth.id1212.server;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
    private static final int port = 1234;
    private static final ArrayList<ClientHandler> connected_clients = new ArrayList<>();

    public static void main(String[] args) {
        startup_server();
    }

    private static void startup_server() {
        try (ServerSocket server_socket = new ServerSocket(port)) {
            System.out.println("Server started; open on port:" + port + ".");

            while (!server_socket.isClosed()) {
                Socket client_socket = server_socket.accept();
                String client_IP = client_socket.getInetAddress().getHostAddress();
                System.out.println(client_IP + " connected.");

                ClientHandler new_client = new ClientHandler(client_socket, connected_clients);
                connected_clients.add(new_client);
                new Thread(new_client).start();
            }

        } catch (Exception exception) {
            logExceptionToFile(exception);
        }
    }

    static void logExceptionToFile(Exception exception) {
        String filePath = "Task_1\\src\\main\\se\\kth\\id1212\\server\\exception_log.txt";
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filePath, true));
            exception.printStackTrace(writer);
            writer.println();
            writer.flush();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }
}
