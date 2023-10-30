package main.se.kth.id1212.startup;

import main.se.kth.id1212.imap_retrieve.EmailRetriever;
import main.se.kth.id1212.smtp_send.EmailSender;

import java.time.LocalTime;
import java.util.Scanner;

/**
 * Main class for the program.
 */
public class Main {

    /**
     * Main method for the program that starts the email retriever and email sender. In the first part of the method,
     * the email retriever is started and the latest email is retrieved. The user is then prompted to enter an email
     * address to send an email to. The email sender is then started and the a pre-defined message is sent to the
     * recipient.
     * @param args  Command line arguments, not used.
     */
    public static void main(String[] args) {
        EmailRetriever emailRetriever = new EmailRetriever();
        emailRetriever.get_latest_email();
        emailRetriever.logout();

        System.out.println("Enter recipient email address: ");
        Scanner scanner = new Scanner(System.in);
        String recipient = scanner.nextLine();

        String message = "Date: " + LocalTime.now() + "\r\n" +
                "From: SMTP Test Program <aaak@kth.se>\r\n" +
                "To: Test Subject <" + recipient + ">\r\n" +
                "Subject: Test Subject Line\r\n" +
                "\r\n" +
                "Test Body";

        EmailSender emailSender = new EmailSender();

        emailSender.send_email(recipient, message);
        emailSender.logout();
    }
}
