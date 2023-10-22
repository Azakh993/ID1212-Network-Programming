package main.se.kth.id1212.client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter host IP address: ");
        String host = scanner.nextLine();

        System.out.print("Enter host port:");
        int port = scanner.nextInt();

        establish_connection(host, port);
    }

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

    static void start_message_sending_thread(Socket socket) {
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

            } catch (IOException exception) {
                logExceptionToFile(exception);
            }
        }).start();

    }

    static void message_receiving_thread(Socket socket) {
        new Thread(() -> {
            try {
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream incoming_message_stream = new ObjectInputStream(inputStream);

                String received_message;
                while (!socket.isClosed()) {
                    received_message = (String) incoming_message_stream.readObject();
                    System.out.println(received_message);
                }
            } catch (SocketException socketException) {
                if (!socketException.getMessage().equals("Socket closed")) {
                    logExceptionToFile(socketException);
                }
            } catch (Exception exception) {
                logExceptionToFile(exception);
            }
        }).start();
    }

    private static void close_socket_connection(Socket socket) {
        try {
            socket.close();
        } catch (IOException exception) {
            logExceptionToFile(exception);
        }
    }

    private static void logExceptionToFile(Exception exception) {
        String filePath = "Task_1\\src\\main\\se\\kth\\id1212\\client\\exception_log.txt";
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
