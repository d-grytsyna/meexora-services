package com.meexora.emailservice.service;

import com.meexora.common.dto.EmailTicketDto;
import com.meexora.common.kafka.NotifyUsersEventEditedMessage;
import com.meexora.common.kafka.TicketEmailMessage;
import com.meexora.common.kafka.TicketGenerationMessage;
import com.meexora.emailservice.utils.TicketPdfGenerator;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final TicketPdfGenerator pdfGenerator;

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

    public void sendTickets(TicketEmailMessage message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(message.getUserEmail());
            helper.setSubject("Your Tickets for " + message.getTickets().get(0).getEventTitle());
            helper.setText("Dear " + message.getTickets().get(0).getUserName() + ",\n\n" +
                    "Please find your event tickets attached as PDF files.\n\nEnjoy the event!", false);

            for (int i = 0; i < message.getTickets().size(); i++) {
                EmailTicketDto ticket = message.getTickets().get(i);

                byte[] pdfBytes = pdfGenerator.generateTicket(
                        ticket.getUserName(),
                        ticket.getEventTitle(),
                        ticket.getEventLocation(),
                        ticket.getEventDate(),
                        ticket.getPrice(),
                        ticket.getQrCode()
                );

                ByteArrayResource resource = new ByteArrayResource(pdfBytes);
                helper.addAttachment("ticket-" + (i + 1) + ".pdf", resource);
            }

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new MailSendException("Failed to send ticket email", e);
        }
    }

    public void sendRefundTickets(TicketGenerationMessage message) {
        if ("REFUNDED".equals(message.getStatus())) {
            sendRefundSuccessEmail(message);
        } else if ("REFUND_FAILED".equals(message.getStatus())) {
            sendRefundFailedEmail(message);
        } 
    }
    private void sendRefundSuccessEmail(TicketGenerationMessage message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);

            helper.setTo(message.getUserEmail());
            helper.setSubject("Your booking has been refunded: " + message.getEventTitle());

            String body =
                    "Your booking for \"" + message.getEventTitle() + "\" on " +
                    message.getEventDate().toLocalDate() + " has expired and was automatically cancelled.\n\n" +
                    "A refund of €" + message.getTotalPrice() + " is being processed and should appear on your payment method soon.\n\n" +
                    "If you have any questions, feel free to contact our support.\n\n" +
                    "— Meexora Team";

            helper.setText(body, false);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailSendException("Failed to send refund success email", e);
        }
    }
    private void sendRefundFailedEmail(TicketGenerationMessage message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);

            helper.setTo(message.getUserEmail());
            helper.setSubject("Refund failed: " + message.getEventTitle());

            String body =
                    "We attempted to refund your payment for \"" + message.getEventTitle() + "\", but the operation failed.\n\n" +
                    "Please contact support to manually resolve the issue. Your booking is marked as expired.\n\n" +
                    "— Meexora Team";

            helper.setText(body, false);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailSendException("Failed to send refund failed email", e);
        }
    }


    public void sendWatchingUpdate(TicketGenerationMessage message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);

            helper.setTo(message.getUserEmail());
            helper.setSubject("Your booking has been confirmed: " + message.getEventTitle());

            String body =
                    "Good news!\n\n" +
                            "Tickets for the event \"" + message.getEventTitle() + "\" have become available.\n" +
                            "Your monitored booking has been successfully confirmed and reserved.\n\n" +
                            "You now have limited time - 60 minutes to complete the payment before your reservation expires.\n" +
                            "Please visit Meexora and complete the payment to secure your tickets.\n\n" +
                            "— Meexora Team";

            helper.setText(body, false);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new MailSendException("Failed to send booking confirmation for watching update", e);
        }
    }

    public void sendEventUpdatedEmails(NotifyUsersEventEditedMessage message) {
        String subject = "Event Updated Notification";

        StringBuilder contentBuilder = new StringBuilder("Dear user,\n\n");
        contentBuilder.append("The event you booked has been updated.\n\n");

        if (Boolean.TRUE.equals(message.getLocationChanged()) && message.getLocation() != null) {
            contentBuilder.append("New Location: ").append(message.getLocation()).append("\n");
        }

        if (Boolean.TRUE.equals(message.getDateTimeChanged()) && message.getDateTime() != null) {
            try {
                OffsetDateTime dateTime = OffsetDateTime.parse(message.getDateTime());
                String formattedDate = dateTime.format(
                        DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale.ENGLISH)
                );
                contentBuilder.append("New Date & Time: ").append(formattedDate).append("\n");
            } catch (Exception e) {
                contentBuilder.append("New Date & Time: ").append(message.getDateTime()).append("\n");
            }
        }

        contentBuilder.append("\nPlease check your app for the latest event details.\n\n");
        contentBuilder.append("Best regards,\nWanderly Team");

        String finalContent = contentBuilder.toString();

        for (String email : message.getUserEmails()) {
            sendSimpleEmail(email, subject, finalContent);
        }
    }

    private void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(text);
            mailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }
}
