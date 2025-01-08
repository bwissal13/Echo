package org.example.echo01.auth.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendOtpEmail(String to, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        Context context = new Context();
        context.setVariable("otp", otp);
        
        String htmlContent = templateEngine.process("email/otp-template", context);
        
        helper.setTo(to);
        helper.setSubject("Your OTP Code");
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }

    @Async
    public void sendVerificationEmail(String to, String verificationLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        Context context = new Context();
        context.setVariable("verificationLink", verificationLink);
        
        String htmlContent = templateEngine.process("email/verification-template", context);
        
        helper.setTo(to);
        helper.setSubject("Verify Your Email");
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
} 