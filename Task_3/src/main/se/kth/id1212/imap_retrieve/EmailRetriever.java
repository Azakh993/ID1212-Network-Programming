package main.se.kth.id1212.imap_retrieve;

import main.se.kth.id1212.util.ExceptionLogger;
import main.se.kth.id1212.util.UserCredentials;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class EmailRetriever {

    private SSLSocket socket;
    private final UserCredentials user_credentials;
    private int message_count = 1;
    private String current_command = "";
    private PrintWriter input_to_server;
    private BufferedReader output_from_server;

    public EmailRetriever() {
        this.user_credentials = new UserCredentials();
        setup_connection();
        login();
    }

    public void get_latest_email() {
        select_inbox();
        retrieve_latest_received_email();
    }

    private void setup_connection() {
        try {
            SSLSocketFactory socket_factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            String IMAP_SERVER = "webmail.kth.se";
            int IMAP_PORT = 993;

            System.out.println("Connecting to " + IMAP_SERVER + " on port " + IMAP_PORT);
            socket = (SSLSocket) socket_factory.createSocket(IMAP_SERVER, IMAP_PORT);

            System.out.println("Connected to " + socket.getRemoteSocketAddress());
            socket.setUseClientMode(true);

            System.out.println("Starting handshake");
            socket.startHandshake();
            System.out.println("Handshake complete");

            input_to_server = new PrintWriter(socket.getOutputStream());
            output_from_server = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            output_response();
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }

    private void login() {
        String username = this.user_credentials.getUsername();
        String password = this.user_credentials.getPassword();
        String prefix = "a00" + message_count;
        current_command = "LOGIN";
        String message = prefix + " " + current_command + " " + username + " " + password + "\r\n";

        send_command(message);
        output_response();
        message_count++;
    }

    private void select_inbox() {
        String prefix = "a00" + message_count;
        current_command = "SELECT";
        String mailbox = "INBOX";
        String message = prefix + " " + current_command + " " + mailbox + "\r\n";

        send_command(message);
        output_response();
        message_count++;
    }

    private void retrieve_latest_received_email() {
        String prefix = "a00" + message_count;
        current_command = "FETCH";
        String message_number = "1";
        String data_item = "BODY[1]";
        String message = prefix + " " + current_command + " " + message_number + " " + data_item + "\r\n";

        send_command(message);
        output_response();
        message_count++;
    }

    public void logout() {
        String prefix = "a00" + message_count;
        current_command = "LOGOUT";
        String message = prefix + " " + current_command + "\r\n";

        send_command(message);
        output_response();
        close_connection();
    }

    private void send_command(String message) {
        if (current_command.equals("LOGIN") || current_command.equals("LOGOUT")) {
            String password = this.user_credentials.getPassword();
            System.out.println("S: " + message.replace(password, "[OMITTED]"));
        } else {
            System.out.println("S: " + message);
        }
        input_to_server.write(message);
        input_to_server.flush();
    }

    private void output_response() {
        String received_output;
        try {
            while ((received_output = output_from_server.readLine()) != null) {
                System.out.println("R: " + received_output);
                if (end_of_response(received_output)) {
                    System.out.println();
                    break;
                }
            }
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }

    private void close_connection() {
        try {
            socket.close();
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }

    private boolean end_of_response(String received_output) {
        if (current_command.equals("") && received_output.startsWith("* OK")) {
            return true;
        } else return received_output.startsWith("a00" + message_count + " OK");
    }
}