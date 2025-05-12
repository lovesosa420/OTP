package ru.otp_codes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailNotificationService {

    private String username;
    private String password;
    private String fromEmail;
    private Session session;
    private static Logger logger;


    public EmailNotificationService() {
        Properties config = loadConfig();
        logger = LoggerFactory.getLogger(EmailNotificationService.class);
        this.username = config.getProperty("email.username");
        this.password = config.getProperty("email.password");
        this.fromEmail = config.getProperty("email.from");
        this.session = Session.getInstance(config, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(EmailNotificationService.class.getClassLoader()
                    .getResourceAsStream("email.properties"));
            return props;
        } catch (Exception e) {
            logger.error("Failed to load email configuration");
            throw new RuntimeException("Failed to load email configuration", e);
        }
    }

    public boolean sendCode(String toEmail, String code) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Your OTP Code");
            message.setText("Your verification code is: " + code);
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            logger.error("Failed to send email");
            return false;
        }
    }
}
