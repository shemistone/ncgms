/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.util;

import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import static ncgms.util.Protocol.SMTPS;
import static ncgms.util.Protocol.TLS;

/**
 *
 * @author root
 */
public class EmailSender {

    private final int port = 587; // Use port 465 for SSL
    private final String host = "smtp.gmail.com";
    private final String from = "shemistone@gmail.com";
    boolean authenticate = true;
    private final String username = "shemistone";
    private final String password = "0-Gmail-Burton";
    private final Protocol protocol = Protocol.TLS;
    public boolean debug = true;

    public void sendMail(String to, String subject, String body) throws MessagingException {
        System.out.println(to + " " + subject + " " + body);
        // Properties for the SMTP provider
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        switch (protocol) {
            case SMTPS:
                properties.put("mail.smtp.ssl.enable", true);
                break;
            case TLS:
                properties.put("mail.smtp.starttls.enable", true);
                break;
        }

        // In case SMTP authentication is enabled
        Authenticator authenticator = null;
        if (authenticate) {
            properties.put("mail.smtp.auth", true);
            authenticator = new Authenticator() {
                private PasswordAuthentication pa
                        = new PasswordAuthentication(username, password);

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }

        // Create a session using the properties and authenticator
        Session session = Session.getInstance(properties, authenticator);
        // Enable debugging
        session.setDebug(debug); // Can also be used to print the current session's activity

        //Create a MIME message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        InternetAddress[] address = {new InternetAddress(to)};
        message.setRecipients(Message.RecipientType.TO, address);
        message.setSubject(subject);
        message.setSentDate(new Date());
            // Only for text only email
        // message.setText(body); 

        // For text or html message
        Multipart multipart = new MimeMultipart("alternative");

        MimeBodyPart textPart = new MimeBodyPart();
        String textContent = body;
        textPart.setText(textContent);

        MimeBodyPart htmlPart = new MimeBodyPart();
        String htmlContent = body;
        htmlPart.setContent(htmlContent, "text/html");
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);
        message.setContent(multipart);

        Transport.send(message);

    }

}
