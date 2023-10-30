package main.se.kth.id1212.client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


/**
 * Starts a client application.
 */
public class ChatClient {

    /**
     * Starts a client application instance.
     * The application takes two command line parameters:
     * 1. The host IP address.
     * 2. The host port.
     * @param args The application does not take any command line parameters.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter host IP address: ");
        String host = scanner.nextLine();

        System.out.print("Enter host port:");
        int port = scanner.nextInt();

        establish_connection(host, port);
    }

    /**
     * Establishes a connection to a server.
     * The connection is established by creating a socket. The socket is used to create two threads:
     * 1. A thread that sends messages to the server.
     * 2. A thread that receives messages from the server.
     * @param host The host IP address.
     * @param port The host port.
     */
    private static void establish_connection(String host, int port) {
        System.out.println("Establishing connection to " + host + ":" + port);

        try {
            Socket socket = new Socket(host, port);
            System.out.println("Connection established!\n");
            message_receiving_thread(socket);
            start_message_sending_thread(socket);

        } catch (Exception exception) {
            System.err.println("Connection could not be established;");
            logExceptionToFile(exception);
        }
    }

    /**
     * Starts a thread that sends messages to the server. The thread is started by creating an
     * ObjectOutputStream that is used to send messages to the server.
     * The thread is terminated when the user enters the command "!exit",
     * or when the server closes the connection (which is detected by the SocketException).
     * @param socket The socket to send messages to.
     */

    private static void start_message_sending_thread(Socket socket) {
        new Thread(() -> {
            try {
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream outgoing_message_stream = new ObjectOutputStream(outputStream);

                while (!socket.isClosed()) {
                    Scanner console_input = new Scanner(System.in);
                    String message = console_input.nextLine();
                    outgoing_message_stream.writeObject(message);
                    outgoing_message_stream.flush();

                    if (message.equals("!exit")) {
                        outgoing_message_stream.close();
                        close_socket_connection(socket);
                        break;
                    }
                }

            } catch (SocketException exception) {
                System.out.println("Server closed connection.");
            } catch (IOException exception) {
                logExceptionToFile(exception);
            }
        }).start();

    }

    /**
     * Starts a thread that receives messages from the server. The thread is started by creating an
     * ObjectInputStream that is used to receive messages from the server.
     * The thread is terminated when the server closes the connection (which is detected by the EOFException),
     * or when the user enters the command "!exit" (which is detected by the SocketException).
     * @param socket The socket to receive messages from.
     */
    private static void message_receiving_thread(Socket socket) {
        new Thread(() -> {
            try {
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream incoming_message_stream = new ObjectInputStream(inputStream);

                String received_message;
                while (!socket.isClosed()) {
                    received_message = (String) incoming_message_stream.readObject();
                    System.out.println(received_message);
                }
            } catch (EOFException exception) {
                System.out.println("Server closed connection.");
            } catch (SocketException exception) {
                System.out.println("Exited chat.");
            } catch (Exception exception) {
                logExceptionToFile(exception);
            }
        }).start();
    }

    /**
     * Closes the socket connection.
     * @param socket The socket to close.
     */
    private static void close_socket_connection(Socket socket) {
        try {
            socket.close();
        } catch (IOException exception) {
            logExceptionToFile(exception);
        }
    }

    /**
     * Logs an exception to a file.
     * @param exception The exception to log.
     */
    private static void logExceptionToFile(Exception exception) {
        String filePath = "Task_1/src/main/se/kth/id1212/client/exception_log.txt";
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
