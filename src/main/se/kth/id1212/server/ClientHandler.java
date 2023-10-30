package main.se.kth.id1212.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static main.se.kth.id1212.server.ChatServer.logExceptionToFile;

/**
 * Handles the communication between the server and a client.
 */
class ClientHandler implements Runnable {
    private final Socket client_socket;
    private final String client_name;
    private final ArrayList<ClientHandler> all_connected_clients;
    private ObjectOutputStream outgoing_message_stream;

    /**
     * Creates a new instance of a client handler.
     * @param client_socket          The socket of the client.
     * @param connected_clients      A list of all connected clients.
     */
    ClientHandler(Socket client_socket, ArrayList<ClientHandler> connected_clients) {
        this.client_socket = client_socket;
        client_name = client_socket.getInetAddress().getHostName();
        all_connected_clients = connected_clients;
    }

    /**
     * Starts the communication between the server and the client. The server waits for incoming messages from the
     * client via the input stream. When a message is received, it is broadcasted to all connected clients via the
     * output stream. If the client sends the message "!exit", its connection is closed and the exit is broadcasted.
     * If the client disconnects without sending "!exit", the server will broadcast this to all connected clients.
     */
    @Override
    public void run() {
        try {
            InputStream inputStream = client_socket.getInputStream();
            ObjectInputStream incoming_message_stream = new ObjectInputStream(inputStream);

            OutputStream outputStream = client_socket.getOutputStream();
            outgoing_message_stream = new ObjectOutputStream(outputStream);

            outgoing_message_stream.writeObject("Welcome " + client_name + "!");
            outgoing_message_stream.flush();

            String incoming_message;

            while (!client_socket.isClosed()) {
                incoming_message = (String) incoming_message_stream.readObject();
                System.out.println(client_name + ": " + incoming_message);
                if (incoming_message.equals("!exit")) {
                    broadcast("has left the chat.");
                    close_connection();
                } else {
                    broadcast(incoming_message);
                }
            }

        } catch (EOFException exception) {
            broadcast("has left the chat.");
            System.out.println(client_name + " disconnected.");
            all_connected_clients.remove(this);
        } catch (Exception exception) {
            logExceptionToFile(exception);
        }

    }

    /**
     * Broadcasts a message to all connected clients except the current client.
     * The message is sent via the output stream of each client in the all_connected_clients ArrayList.
     * @param message   The message to be broadcasted.
     */
    private void broadcast(String message) {
        for (ClientHandler client : all_connected_clients) {
            if (!this.equals(client) && client.outgoing_message_stream != null) {
                try {
                    client.outgoing_message_stream.writeObject(client_name + ": " + message);
                    client.outgoing_message_stream.flush();
                } catch (Exception exception) {
                    logExceptionToFile(exception);
                }
            }
        }
    }

    /**
     * Closes the connection to the client.
     */
    private void close_connection() {
        try {
            all_connected_clients.remove(this);
            outgoing_message_stream.close();
            client_socket.close();
        } catch (IOException exception) {
            logExceptionToFile(exception);
        }
    }
}
