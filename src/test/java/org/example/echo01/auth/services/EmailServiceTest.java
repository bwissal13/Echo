package org.example.echo01.auth.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendOtpEmail() throws MessagingException {
        String to = "test@example.com";
        String otp = "123456";
        
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        when(templateEngine.process(eq("email/otp-template"), any(Context.class))).thenReturn("OTP Email Content");

        emailService.sendOtpEmail(to, otp);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendVerificationEmail() throws MessagingException {
        String to = "test@example.com";
        String verificationLink = "http://example.com/verify";
        
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        when(templateEngine.process(eq("email/verification-template"), any(Context.class))).thenReturn("Verification Email Content");

        emailService.sendVerificationEmail(to, verificationLink);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
} 