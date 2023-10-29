package main.se.kth.id1212.smtp_send;

import main.se.kth.id1212.util.ExceptionLogger;
import main.se.kth.id1212.util.UserCredentials;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;

public class EmailSender {

    private final UserCredentials user_credentials;
    private String current_command = "";
    private PrintWriter input_to_server;
    private BufferedReader output_from_server;

    public EmailSender() {
        this.user_credentials = new UserCredentials();
        setup_connection();
        login();
    }

    public void send_email(String recipient, String message) {
        String username = this.user_credentials.getUsername();
        exchange_with_server("MAIL FROM: <" + username + "@kth.se>");
        exchange_with_server("RCPT TO: <" + recipient + ">");
        exchange_with_server("DATA");
        exchange_with_server(message + "\r\n.");
    }

    private void exchange_with_server(String message) {
        send_command(message);
        output_response();
    }

    private void send_command(String message) {
        if (current_command.equals("AUTH LOGIN")) {
            String password = this.user_credentials.getPassword();
            String encoded_password = Base64.getEncoder().encodeToString(password.getBytes());
            System.out.println("C: " + message.replace(encoded_password, "[PASSWORD]"));
        } else {
            System.out.println("C: " + message);
        }
        input_to_server.write(message + "\r\n");
        input_to_server.flush();
    }

    private void output_response() {
        String received_output;
        try {
            while ((received_output = output_from_server.readLine()) != null) {
                System.out.println("S: " + received_output);
                if (end_of_response(received_output)) {
                    System.out.println();
                    break;
                }
            }
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }

    private boolean end_of_response(String received_output) {
        String response_code = received_output.substring(0, 3);
        for(int i = 0; i < response_code.length(); i++) {
            if(Character.isDigit(response_code.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private void setup_connection() {
        String SMTP_SERVER = "smtp.kth.se";
        int SMTP_PORT = 587;

        try {
            System.out.println("Connecting to " + SMTP_SERVER + " on port " + SMTP_PORT);
            Socket socket = new Socket(SMTP_SERVER, SMTP_PORT);
            input_to_server = new PrintWriter(socket.getOutputStream());
            output_from_server = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output_response();

            exchange_with_server("HELO " + SMTP_SERVER);
            exchange_with_server("STARTTLS");

            SSLSocketFactory socket_factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket ssl_socket = (SSLSocket) socket_factory.createSocket(socket, SMTP_SERVER, SMTP_PORT, true);

            input_to_server = new PrintWriter(ssl_socket.getOutputStream());
            output_from_server = new BufferedReader(new InputStreamReader(ssl_socket.getInputStream()));

            exchange_with_server("HELO " + SMTP_SERVER);
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile(exception);
        }
    }

    private void login() {
        current_command = "AUTH LOGIN";
        exchange_with_server(current_command);

        String username = this.user_credentials.getUsername();
        String encoded_username = Base64.getEncoder().encodeToString(username.getBytes());
        exchange_with_server(encoded_username);

        String password = this.user_credentials.getPassword();
        String encoded_password = Base64.getEncoder().encodeToString(password.getBytes());
        exchange_with_server(encoded_password);
    }

    public void logout() {
        current_command = "QUIT";
        exchange_with_server(current_command);
    }
}