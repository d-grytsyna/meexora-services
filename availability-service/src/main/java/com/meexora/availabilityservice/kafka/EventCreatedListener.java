package com.meexora.availabilityservice.kafka;


import com.meexora.availabilityservice.service.AvailabilityService;
import com.meexora.common.kafka.EventCreatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventCreatedListener {

    private final AvailabilityService availabilityService;
    @KafkaListener(
            topics = "${kafka.topic.event-created}",
            groupId = "availability-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEventCreated(EventCreatedMessage message) {
        System.out.println("Received event " + message.getEventId());
        availabilityService.saveTicketAvailability(message);
    }
}
