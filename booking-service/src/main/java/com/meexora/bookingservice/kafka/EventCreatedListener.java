package com.meexora.bookingservice.kafka;


import com.meexora.bookingservice.service.TicketAvailabilityService;
import com.meexora.common.kafka.EventCreatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventCreatedListener {

    private final TicketAvailabilityService availabilityService;
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
