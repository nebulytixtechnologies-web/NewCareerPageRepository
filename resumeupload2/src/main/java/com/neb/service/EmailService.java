package com.neb.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
/**
 * Service responsible for sending application-related emails.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Sends a plain-text email to a specified recipient.
     *
     * @param to      Recipient email address
     * @param subject Subject of the email
     * @param text    Body content of the email
     */
    public void sendApplicationMail(String to, String subject, String text) 
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
