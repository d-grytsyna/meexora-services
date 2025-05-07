package com.meexora.paymentservice.kafka;

import com.meexora.common.kafka.PaymentCompletedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCompletedProducer {

    private final KafkaTemplate<String, PaymentCompletedMessage> kafkaTemplate;

    @Value("${kafka.topic.payment-completed}")
    private String paymentCompletedTopic;

    public void sendPaymentCompleted(PaymentCompletedMessage message) {
        kafkaTemplate.send(paymentCompletedTopic, message.getBookingId().toString(), message);
    }
}
