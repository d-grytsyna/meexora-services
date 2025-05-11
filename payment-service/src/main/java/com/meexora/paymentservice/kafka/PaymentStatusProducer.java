package com.meexora.paymentservice.kafka;

import com.meexora.common.kafka.PaymentStatusUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentStatusProducer {

    private final KafkaTemplate<String, PaymentStatusUpdateMessage> kafkaTemplate;

    @Value("${kafka.topic.payment-status-update}")
    private String paymentStatusUpdateTopic;

    public void sendPaymentStatusUpdate(PaymentStatusUpdateMessage message) {
        kafkaTemplate.send(paymentStatusUpdateTopic, message.getBookingId().toString(), message);
    }
}
