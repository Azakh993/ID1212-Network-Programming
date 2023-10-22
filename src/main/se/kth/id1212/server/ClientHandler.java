package main.se.kth.id1212.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import static main.se.kth.id1212.server.ChatServer.logExceptionToFile;

class ClientHandler implements Runnable {
    private final Socket client_socket;
    private final String client_name;
    private final ArrayList<ClientHandler> all_connected_clients;
    private ObjectOutputStream outgoing_message_stream;

    ClientHandler(Socket client_socket, ArrayList<ClientHandler> connected_clients) {
        this.client_socket = client_socket;
        client_name = client_socket.getInetAddress().getHostName();
        all_connected_clients = connected_clients;
    }

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

        } catch (Exception exception) {
            logExceptionToFile(exception);
        }

    }

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
