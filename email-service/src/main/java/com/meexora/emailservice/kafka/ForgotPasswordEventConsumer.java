package com.meexora.emailservice.kafka;


import com.meexora.common.kafka.AccountVerificationMessage;
import com.meexora.emailservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForgotPasswordEventConsumer {
    private final MailService mailService;

    @KafkaListener(topics = "${kafka.topic.user-forgot-password-request}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(AccountVerificationMessage message) {
        System.out.println("Received email: " + message.getEmail());
        System.out.println("Received code: " + message.getVerificationCode());
        mailService.sendForgotPasswordEmail(message.getEmail(), message.getVerificationCode());
    }
}
