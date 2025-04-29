package com.meexora.authservice.kafka;

import com.meexora.common.kafka.AccountVerificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationEventProducer {

    private final KafkaTemplate<String, AccountVerificationMessage> kafkaTemplate;

    @Value("${kafka.topic.user-registration-request}")
    private String registrationTopic;

    public void sendRegistrationRequest(AccountVerificationMessage message) {
        kafkaTemplate.send(registrationTopic, message);
    }
}
