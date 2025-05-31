package com.meexora.ticketmanagementservice.kafka;


import com.meexora.common.kafka.TicketGenerationMessage;
import com.meexora.ticketmanagementservice.service.TicketGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketGenerationListener {

    private final TicketGenerationService ticketGenerationService;
    @KafkaListener(
            topics = "${kafka.topic.booking-confirmed}",
            groupId = "booking-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleBookingConfirmed(TicketGenerationMessage ticketGenerationMessage) {
        ticketGenerationService.handleTicketGeneration(ticketGenerationMessage);
    }
}
