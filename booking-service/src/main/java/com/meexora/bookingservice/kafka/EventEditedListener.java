package com.meexora.bookingservice.kafka;

import com.meexora.bookingservice.service.BookingService;
import com.meexora.common.kafka.EventEditedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventEditedListener {

    private final BookingService bookingService;

    @KafkaListener(
            topics = "${kafka.topic.event-edited}",
            groupId = "booking-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEventEdited(EventEditedMessage message) {
        bookingService.handleEventEdited(message);
    }
}
