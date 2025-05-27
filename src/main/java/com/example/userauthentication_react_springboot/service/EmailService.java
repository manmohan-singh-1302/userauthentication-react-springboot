package com.example.userauthentication_react_springboot.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendVerificationEmail(String to, String token) throws MessagingException {
        Context context = new Context();
        context.setVariable("verificationLink", "http://localhost:3000/verify-email?token=" + token);
        
        String emailContent = templateEngine.process("verification-email", context);
        
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom("your-email@gmail.com");
        helper.setTo(to);
        helper.setSubject("Verify Your Email");
        helper.setText(emailContent, true);
        
        emailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        Context context = new Context();
        context.setVariable("resetLink", "http://localhost:3000/reset-password?token=" + token);
        
        String emailContent = templateEngine.process("password-reset-email", context);
        
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom("your-email@gmail.com");
        helper.setTo(to);
        helper.setSubject("Reset Your Password");
        helper.setText(emailContent, true);
        
        emailSender.send(message);
    }
} 