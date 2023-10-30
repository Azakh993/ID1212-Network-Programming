package main.se.kth.id1212.imap_retrieve;

import main.se.kth.id1212.util.ExceptionLogger;
import main.se.kth.id1212.util.UserCredentials;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * This class is responsible for retrieving the latest email from the user's inbox.
 */
public class EmailRetriever {

    private SSLSocket socket;
    private final UserCredentials user_credentials;
    private int message_count = 1;
    private String current_command = "";
    private PrintWriter input_to_server;
    private BufferedReader output_from_server;

    /**
     * Constructor for the EmailRetriever class.
     */
    public EmailRetriever() {
        this.user_credentials = new UserCredentials();
        setup_connection();
        login();
    }

    /**
     * Retrieves the latest email from the user's inbox.
     */
    public void get_latest_email() {
        select_inbox();
        retrieve_latest_received_email();
    }

    /**
     * Sets up the connection to the IMAP server. First, the socket is created using the SSLSocketFactory. Then, the
     * socket is connected to the IMAP server. After that, the handshake is started and completed. Finally, the input
     * and output streams are created.
     */
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
            ExceptionLogger.logExceptionToFile("EmailRetriever", exception);
        }
    }

    /**
     * Logs in to the IMAP server using the user's credentials. The username and password are retrieved from the
     * UserCredentials object. The message is constructed and sent to the server. The response is then outputted.
     * Finally, the message count is incremented.
     */
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

    /**
     * Selects the user's inbox. The message is constructed and sent to the server. The response is then outputted.
     * Finally, the message count is incremented.
     */
    private void select_inbox() {
        String prefix = "a00" + message_count;
        current_command = "SELECT";
        String mailbox = "INBOX";
        String message = prefix + " " + current_command + " " + mailbox + "\r\n";

        send_command(message);
        output_response();
        message_count++;
    }

    /**
     * Retrieves the latest received email from the user's inbox. The message is constructed and sent to the server.
     * The response is then outputted. Finally, the message count is incremented.
     */
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

    /**
     * Logs out from the IMAP server. The message is constructed and sent to the server. The response is then
     * outputted. Finally, the connection is closed.
     */
    public void logout() {
        String prefix = "a00" + message_count;
        current_command = "LOGOUT";
        String message = prefix + " " + current_command + "\r\n";

        send_command(message);
        output_response();
        close_connection();
    }

    /**
     * Send the given message to the server. If the current command is LOGIN, the password is omitted from the message.
     * The message is then sent to the server.
     * @param message       The message to be sent to the server.
     */
    private void send_command(String message) {
        if (current_command.equals("LOGIN")) {
            String password = this.user_credentials.getPassword();
            System.out.println("S: " + message.replace(password, "[OMITTED]"));
        } else {
            System.out.println("S: " + message);
        }
        input_to_server.write(message);
        input_to_server.flush();
    }

    /**
     * Outputs the response from the server. In the while loop, the response is read line by line and output. If the
     * line marks the end of the response, the loop is broken.
     */
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
            ExceptionLogger.logExceptionToFile("EmailRetriever", exception);
        }
    }

    /**
     * Closes the connection to the IMAP server.
     */
    private void close_connection() {
        try {
            socket.close();
        } catch (Exception exception) {
            ExceptionLogger.logExceptionToFile("EmailRetriever", exception);
        }
    }

    /**
     * Checks if the given response line marks the end of the response from the server. If the current command is
     * an empty string and the response line starts with "* OK", return true. Also, if the response line starts with
     * the last sent prefix followed by " OK", return true. Otherwise, return false.
     * @param received_output      The response line from the server.
     * @return                     True response line marks end of response, and false otherwise.
     */
    private boolean end_of_response(String received_output) {
        if (current_command.equals("") && received_output.startsWith("* OK")) {
            return true;
        } else return received_output.startsWith("a00" + message_count + " OK");
    }
}