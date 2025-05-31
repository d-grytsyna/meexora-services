package com.meexora.authservice.kafka;

import com.meexora.common.kafka.AccountVerificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForgotPasswordEventProducer {


    private final KafkaTemplate<String, AccountVerificationMessage> kafkaTemplate;

    @Value("${kafka.topic.user-forgot-password-request}")
    private String forgotPasswordTopic;

    public void sendForgotPasswordRequest(AccountVerificationMessage message) {
        kafkaTemplate.send(forgotPasswordTopic, message);
    }

}
