package com.meexora.ticketmanagementservice.kafka;

import com.meexora.common.kafka.TicketEmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketEmailProducer {
    private final KafkaTemplate<String, TicketEmailMessage> kafkaTemplate;

    @Value("${kafka.topic.ticket-email}")
    private String topic;

    public void sendTicketEmailEvent(TicketEmailMessage message) {
        kafkaTemplate.send(topic, message.getBookingId().toString(), message);
    }
}
