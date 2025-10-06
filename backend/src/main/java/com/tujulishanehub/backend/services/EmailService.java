package com.tujulishanehub.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.from:noreply@tujulishanehub.com}")
    private String fromEmail;
    
    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    public void sendEmail(String to, String subject, String body) {
        logger.info("Sending email to {} with subject={}", to, subject);
        
        // If email is not configured, just log the email content
        if (!emailEnabled || mailSender == null) {
            logger.warn("⚠️ Email service not configured. Logging email content instead:");
            logger.warn("📧 To: {}", to);
            logger.warn("📧 Subject: {}", subject);
            logger.warn("📧 Body: {}", body);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (MailException e) {
            // Log full exception so root cause is visible in application logs
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            logger.warn("📧 Email content that failed to send:");
            logger.warn("📧 To: {}", to);
            logger.warn("📧 Subject: {}", subject);
            logger.warn("📧 Body: {}", body);
            // Don't throw exception - just log it so login can continue
        }
    }
}
