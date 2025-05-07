package com.meexora.bookingservice.kafka;

import com.meexora.common.kafka.BookingCreatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingCreatedProducer {

    private final KafkaTemplate<String, BookingCreatedMessage> kafkaTemplate;

    @Value("${kafka.topic.booking-created}")
    private String topic;

    public void sendBookingCreatedEvent(BookingCreatedMessage message) {
        kafkaTemplate.send(topic, message.getBookingId().toString(), message);
    }
}
