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

/**
 * Class that sends an email to a recipient using the SMTP protocol.
 */
public class EmailSender {

    private final UserCredentials user_credentials;
    private String current_command = "";
    private PrintWriter input_to_server;
    private BufferedReader output_from_server;

    /**
     * Constructor that sets up the connection to the SMTP server and logs in.
     */
    public EmailSender() {
        this.user_credentials = new UserCredentials();
        setup_connection();
        login();
    }

    /**
     * Sends an email to a recipient. First, the sender is sent to the server. Next, the recipient is sent to the
     * server. Then, the message itself is sent to the server. Finally, a period is sent to the server to indicate
     * that the email is finished and should be sent from the server.
     * @param recipient The email-address of recipient of the email.
     * @param message   The message of the email.
     */
    public void send_email(String recipient, String message) {
        String username = this.user_credentials.getUsername();
        exchange_with_server("MAIL FROM: <" + username + "@kth.se>");
        exchange_with_server("RCPT TO: <" + recipient + ">");
        exchange_with_server("DATA");
        exchange_with_server(message + "\r\n.");
    }

    /**
     * Sends a command to the server and outputs the response from the server.
     * @param message The command to send to the server.
     */
    private void exchange_with_server(String message) {
        send_command(message);
        output_response();
    }

    /**
     * Sends a command to the server. If the command is the login command, the password is replaced with "[PASSWORD]"
     * in the output. Either way, the command is then sent to the server.
     * @param message The command to send to the server.
     */
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

    /**
     * Outputs the response from the server, line by line. If the current line is the last line of the response, the
     * method stops outputting lines.
     */
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
            ExceptionLogger.logExceptionToFile("EmailSender", exception);
        }
    }

    /**
     * Checks if the given string is the last line of a response from the server. First, first three characters of the
     * string are isolated. Then, each of these character is checked to see if it is a digit. If it is, then it is
     * assumed that the string is the last line of a response from the server, and true is returned. Otherwise, false
     * is returned.
     * @param received_output   The string to check.
     * @return                  True if the string is the last line of a response from the server, false otherwise.
     */
    private boolean end_of_response(String received_output) {
        String response_code = received_output.substring(0, 3);
        for(int i = 0; i < response_code.length(); i++) {
            if(Character.isDigit(response_code.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets up the connection to the SMTP server. First, the socket connection is set up and the input and output
     * streams are created. The initial response from the server is then output. Next, the connection is secured by
     * first sending "HELO" to the server and then "STARTTLS". Finally, the communication is secured by creating a
     * SSLSocket and using it to create the input and output streams.
     */
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
            ExceptionLogger.logExceptionToFile("EmailSender", exception);
        }
    }

    /**
     * Logs in to the SMTP server using the user's credentials. First, the login command is sent to the server. Then,
     * the username and password are subsequently sent to the server, encoded using Base64. After each respective
     * command is sent, the response from the server is output.
     */
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

    /**
     * Logs out of the SMTP server by sending the "QUIT" command to the server. The response from the server is then
     * output.
     */
    public void logout() {
        current_command = "QUIT";
        exchange_with_server(current_command);
    }
}