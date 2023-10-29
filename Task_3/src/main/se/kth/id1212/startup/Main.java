package main.se.kth.id1212.startup;

import main.se.kth.id1212.imap_retrieve.EmailRetriever;
import main.se.kth.id1212.smtp_send.EmailSender;

import java.time.LocalTime;
import java.util.Scanner;

public class Main {
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
