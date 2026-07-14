package com.tujulishanehub.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.username:}")
    private String mailUsername;

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

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        logger.info("Sending HTML email to {} subject={}", to, subject);
        if (!emailEnabled || mailSender == null) {
            logger.warn("⚠️ Email service not configured. HTML email logged only:");
            logger.warn("📧 To: {} | Subject: {}", to, subject);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            logger.info("HTML email sent successfully to {}", to);
        } catch (MessagingException | MailException e) {
            logger.error("Failed to send HTML email to {}: {}", to, e.getMessage(), e);
        }
    }

    @PostConstruct
    private void logMailConfiguration() {
        try {
            logger.info("Mail startup: app.email.enabled={} mailSenderConfigured={} host={} usernamePresent={}",
                    emailEnabled, mailSender != null, mailHost == null || mailHost.isEmpty() ? "(none)" : mailHost,
                    (mailUsername == null || mailUsername.isEmpty()) ? false : true);
        } catch (Exception e) {
            // don't let logging cause startup failure
            logger.warn("Unable to log mail configuration: {}", e.getMessage());
        }
    }
}
