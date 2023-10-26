package main.se.kth.id1212.server.view;

public class StringResourcesView {

    static String generate_HTTP_header_with_cookie(String session_id) {
        String header =
            "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html\r\n" +
            "Set-Cookie: session_id=" + session_id + "\r\n" +
            "\r\n";
        return header;
    }

    static String generate_HTTP_header(String session_id) {
        String header =
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n";
        return header;
    }

    static String generateDefaultHTML( int number_of_guesses, String guess_outcome) {
        String html =
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
        return html;
    }

    static String generateRestartHTML(int number_of_guesses, String guess_outcome) {
        String html =
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
        return html;
    }
}
