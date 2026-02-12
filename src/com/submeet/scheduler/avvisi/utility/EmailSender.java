package com.submeet.scheduler.avvisi.utility;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailSender {

    // Create a thread for sending an email
    public static void sendEmail(String recipientEmail, String subject, String content) {
        new Thread(() -> sendEmailInternal(recipientEmail, subject, content)).start();
    }

    // Send email logic
    private static void sendEmailInternal(String recipientEmail, String subject, String content) {
        final String senderEmail = "submeetapp@gmail.com";
        final String appPassword = "kyalxwsukwhrwypm";

        // Impostazioni SMTP per Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Crea la sessione SMTP con autenticazione
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, appPassword);
            }
        });

        try {
            // Costruzione del messaggio
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(content);

            // Invio
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}