package com.meexora.bookingservice.service;


import com.meexora.bookingservice.model.TicketAvailability;
import com.meexora.bookingservice.repository.TicketAvailabilityRepository;
import com.meexora.common.exception.NotFoundException;
import com.meexora.common.kafka.EventCreatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketAvailabilityService {

    private final TicketAvailabilityRepository ticketAvailabilityRepository;

    public void saveTicketAvailability(EventCreatedMessage eventCreatedMessage) {
        TicketAvailability availability = TicketAvailability.builder()
                .eventId(eventCreatedMessage.getEventId())
                .remainingTickets(eventCreatedMessage.getTotalTickets())
                .build();
        ticketAvailabilityRepository.save(availability);

    }

    public boolean checkTicketAvailability(String eventId) {
        return ticketAvailabilityRepository.findByEventId(UUID.fromString(eventId))
                .map(ticketAvailability -> ticketAvailability.getRemainingTickets() > 0)
                .orElseThrow(() -> new NotFoundException("Ticket availability not found for eventId: " + eventId));
    }


}
