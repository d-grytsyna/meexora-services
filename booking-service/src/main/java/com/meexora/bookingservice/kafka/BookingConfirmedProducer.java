package com.meexora.bookingservice.kafka;

import com.meexora.common.kafka.TicketGenerationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingConfirmedProducer {
    private final KafkaTemplate<String, TicketGenerationMessage> kafkaTemplate;

    @Value("${kafka.topic.booking-confirmed}")
    private String topic;

    public void sendBookingUpdatedEvent(TicketGenerationMessage message) {
        kafkaTemplate.send(topic, message.getBookingId().toString(), message);
    }
}
