package com.meexora.eventservice.service;

import com.meexora.common.kafka.EventCreatedMessage;
import com.meexora.eventservice.dto.request.CreateEventRequest;
import com.meexora.eventservice.dto.response.EventResponse;
import com.meexora.eventservice.kafka.EventCreatedProducer;
import com.meexora.eventservice.mapper.EventMapper;
import com.meexora.eventservice.model.Event;
import com.meexora.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventCreatedProducer eventCreatedProducer;

    public EventResponse createEvent(CreateEventRequest request, UUID creatorId) {
        Event event = eventMapper.toEntity(request, creatorId);
        Event saved = eventRepository.save(event);
        EventCreatedMessage message = new EventCreatedMessage(
                saved.getId(),
                saved.getTotalTickets(),
                saved.getDate()
        );
        eventCreatedProducer.sendEventCreated(message);
        return eventMapper.toResponse(saved);
    }
}
