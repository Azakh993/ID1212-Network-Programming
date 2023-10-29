package main.se.kth.id1212.startup;

import main.se.kth.id1212.imap_retrieve.EmailRetriever;

public class Main {
    public static void main(String[] args) {
        EmailRetriever emailRetriever = new EmailRetriever();
        emailRetriever.get_latest_email();
    }
}
