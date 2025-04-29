package com.meexora.emailservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String email, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Meexora Account Verification");
            message.setText(
                    "Hello,\n\n" +
                            "Thank you for registering at Meexora!\n\n" +
                            "Here is your verification code: " + verificationCode + "\n\n" +
                            "If you did not request this registration, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "The Meexora Team"
            );
            mailSender.send(message);
            log.info("Verification email successfully sent to {}", email);
        } catch (MailException ex) {
            log.error("Failed to send verification email to {}. Reason: {}", email, ex.getMessage(), ex);
        }
    }
    public void sendForgotPasswordEmail(String email, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Meexora Account Verification");
            message.setText(
                    "Hello,\n\n" +
                            "We received a request to reset the password for your Meexora account.\n\n" +
                            "Here is your password reset code:\n\n" +
                            verificationCode + "\n\n" +
                            "If you did not request a password reset, you can safely ignore this email.\n\n" +
                            "For your security, this code will expire in 10 minutes.\n\n" +
                            "Best regards,\n" +
                            "The Meexora Team"
            );
            mailSender.send(message);
            log.info("Reset password email successfully sent to {}", email);
        } catch (MailException ex) {
            log.error("Failed to send reset password email to {}. Reason: {}", email, ex.getMessage(), ex);
        }
    }

}
