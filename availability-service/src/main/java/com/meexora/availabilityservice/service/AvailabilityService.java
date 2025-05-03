package com.meexora.availabilityservice.service;


import com.meexora.availabilityservice.model.TicketAvailability;
import com.meexora.availabilityservice.repository.TicketAvailabilityRepository;
import com.meexora.common.kafka.EventCreatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final TicketAvailabilityRepository ticketAvailabilityRepository;

    public void saveTicketAvailability(EventCreatedMessage eventCreatedMessage) {
        TicketAvailability availability = TicketAvailability.builder()
                .eventId(eventCreatedMessage.getEventId())
                .remainingTickets(eventCreatedMessage.getTotalTickets())
                .build();
        ticketAvailabilityRepository.save(availability);


    }
}
