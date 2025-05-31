package com.meexora.bookingservice.kafka;

import com.meexora.common.kafka.TicketGenerationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WatchingBookingUpdateProducer {
    private final KafkaTemplate<String, TicketGenerationMessage> kafkaTemplate;

    @Value("${kafka.topic.watching-booking-update}")
    private String topic;


    public void sendWatchingBookingUpdatedEvent(TicketGenerationMessage message) {
        kafkaTemplate.send(topic, message.getBookingId().toString(), message);
    }

}
