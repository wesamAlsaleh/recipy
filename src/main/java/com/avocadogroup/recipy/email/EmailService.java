package com.avocadogroup.recipy.email;

import com.avocadogroup.recipy.email.dtos.SimpleEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final String noReplyAddress;

    public EmailService(
            JavaMailSender javaMailSender,
            @Value("${spring.mail.non-reply-email}") String noReplyAddress
            ) {
        this.javaMailSender = javaMailSender;
        this.noReplyAddress = noReplyAddress;
    }

    /**
     * Function to send a simple email using the mailSender (without attachment)
     *
     * @param request the {@link SimpleEmailRequest} object containing the recipient's
     *                email address, email subject, and the message body text
     * @throws RuntimeException if an exception occurs during the email sending process
     */
    public void sendEmail(SimpleEmailRequest request) {
        // Create the email message object (object that holds the email details)
        var message = new SimpleMailMessage();

        // Set the email details from the request
        message.setFrom(noReplyAddress);
        message.setTo(request.getTo());
        message.setSubject(request.getSubject());
        message.setText(request.getBody());

        // Try block to check for exceptions
        try {
            // Send the email
            javaMailSender.send(message);

            System.out.println("Email sent successfully to " + request.getTo());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Function to send email with attachment
//     public void sendEmailWithAttachment(CustomEmailRequest request) {}
}
