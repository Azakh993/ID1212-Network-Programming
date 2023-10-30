package main.se.kth.id1212.server.view;

/**
 * This class contains all the HTML code that is sent to the client.
 */
public class StringResourcesView {

    /**
     * This method generates the HTTP header that is sent to the client with a cookie.
     * @param session_id The session id that is sent to the client.
     * @return The HTTP header.
     */
    static String generate_HTTP_header_with_cookie(String session_id) {
        return
            "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html\r\n" +
            "Set-Cookie: session_id=" + session_id + "\r\n" +
            "\r\n";
    }

    /**
     * This method generates the HTTP header that is sent to the client.
     * @return The HTTP header.
     */
    static String generate_HTTP_header() {
        return
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n";
    }

    /**
     * This method generates the HTML code that is sent to the client during a game session.
     * @param number_of_guesses The number of guesses the user has made.
     * @param guess_outcome The outcome of the last guess.
     * @return The HTML code.
     */
    static String generateDefaultHTML( int number_of_guesses, String guess_outcome) {
        return
            "<!DOCTYPE html>\n" +
            "<html>\n" +
                "<head>\n" +
                    "<title>Guessing Game</title>\n" +
                "</head>\n" +
                "<body>\n" +
                    "<h1>Guessing Game</h1>\n" +
                    "<p>Guess a number between 1 and 100</p>\n" +
                    "<form action=\"\" method=\"GET\">\n" +
                        "<input type=\"text\" name=\"guess\">\n" +
                        "<input type=\"submit\" value=\"Guess\">\n" +
                    "</form>\n" +
                    "<p>Number of guesses made: " + number_of_guesses + "</p>\n" +
                    "<p>" + guess_outcome + "</p>\n" +
                "</body>\n" +
            "</html>";
    }

    /**
     * This method generates the HTML code that is sent to the client when the user has guessed the correct number.
     * @param number_of_guesses The number of guesses the user has made.
     * @param guess_outcome The outcome of the last guess.
     * @return The HTML code.
     */
    static String generateRestartHTML(int number_of_guesses, String guess_outcome) {
        return
            "<!DOCTYPE html>\n" +
            "<html>\n" +
                "<head>\n" +
                    "<title>Guessing Game</title>\n" +
                "</head>\n" +
                "<body>\n" +
                    "<h1>Guessing Game</h1>\n" +
                    "<p>Guess a number between 1 and 100</p>\n" +
                    "<form action=\"\" method=\"GET\">\n" +
                        "<input type=\"text\" name=\"guess\">\n" +
                        "<input type=\"submit\" value=\"Guess\">\n" +
                    "</form>\n" +
                    "<p>Number of guesses made: " + number_of_guesses + "</p>\n" +
                    "<p>" + guess_outcome + "</p>\n" +
                    "<form action=\"\" method=\"GET\">\n" +
                        "<input type=\"hidden\" name=\"restart\" value=\"true\">\n" +
                        "<input type=\"submit\" value=\"Restart\">\n" +
                    "</form>\n" +
                "</body>\n" +
            "</html>";
    }
}
