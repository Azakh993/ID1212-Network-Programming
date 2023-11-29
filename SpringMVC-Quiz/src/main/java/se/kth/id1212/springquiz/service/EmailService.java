package se.kth.id1212.springquiz.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import se.kth.id1212.springquiz.util.ExceptionLogger;

import java.io.File;
import java.util.Properties;
import java.util.Scanner;

@Service
@PropertySource("classpath:email.properties")
public class EmailService {

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private String smtpPort;

    private String username;
    private String password;

    public void sendEmail(String to, String subject, String body) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username + "@kth.se"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException exception) {
            ExceptionLogger.log(exception);
        }
    }

    private void setCertificates() {
        String filePath = "SpringMVC-Quiz/src/main/resources/credentials.txt";
        try {
            Scanner file_content = new Scanner(new File(filePath));
            username = file_content.nextLine();
            password = file_content.nextLine();
            file_content.close();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }
}
