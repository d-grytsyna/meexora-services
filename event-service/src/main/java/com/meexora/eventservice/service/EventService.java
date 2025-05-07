package com.meexora.eventservice.service;

import com.meexora.common.dto.EventDetailsDto;
import com.meexora.common.exception.NotFoundException;
import com.meexora.common.kafka.EventCreatedMessage;
import com.meexora.eventservice.dto.request.CreateEventRequest;
import com.meexora.eventservice.dto.response.EventResponse;
import com.meexora.eventservice.dto.response.EventShortResponse;
import com.meexora.eventservice.kafka.EventCreatedProducer;
import com.meexora.eventservice.mapper.EventDetailsMapper;
import com.meexora.eventservice.mapper.EventDtoMapper;
import com.meexora.eventservice.mapper.EventShortResponseMapper;
import com.meexora.eventservice.model.Event;
import com.meexora.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final EventCreatedProducer eventCreatedProducer;
    private final EventDetailsMapper eventDetailsMapper;
    private final EventShortResponseMapper eventShortResponseMapper;

    public EventResponse createEvent(CreateEventRequest request, UUID creatorId) {
        Event event = eventDtoMapper.toEntity(request, creatorId);
        Event saved = eventRepository.save(event);
        EventCreatedMessage message = new EventCreatedMessage(
                saved.getId(),
                saved.getTotalTickets(),
                saved.getDate()
        );
        eventCreatedProducer.sendEventCreated(message);
        return eventDtoMapper.toResponse(saved);
    }

    public EventDetailsDto getEventDetails(UUID eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                ()-> new NotFoundException("Event not found"));
        return eventDetailsMapper.toDto(event);

    }

    public List<EventResponse> getEventsByUserId(UUID userId) {
        List<Event> events = eventRepository.findAllByCreatorId(userId);
        return events.stream()
                .map(eventDtoMapper::toResponse)
                .toList();
    }

    public List<EventShortResponse> findAllEvents() {
        List<Event> events = eventRepository.findAll();
        return eventShortResponseMapper.toEventShortResponse(events);
    }






}
