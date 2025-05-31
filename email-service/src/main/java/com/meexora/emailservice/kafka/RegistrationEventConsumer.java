package com.meexora.emailservice.kafka;

import com.meexora.common.kafka.AccountVerificationMessage;
import com.meexora.emailservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationEventConsumer {

    private final MailService mailService;

    @KafkaListener(topics = "${kafka.topic.user-registration-request}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(AccountVerificationMessage message) {
        mailService.sendVerificationEmail(message.getEmail(), message.getVerificationCode());
    }
}
