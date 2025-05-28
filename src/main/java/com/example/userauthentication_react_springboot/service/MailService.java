package com.example.userauthentication_react_springboot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendMail(String email, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);
            logger.info("Attempting to send email to: {}", email);
            javaMailSender.send(message);
            logger.info("Email sent successfully to: {}", email);
        } catch (MailException e) {
            logger.error("Failed to send email to: {}. Error: {}", email, e.getMessage(), e);
            throw new MailSendException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
