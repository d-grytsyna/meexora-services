package com.meexora.eventservice.service;

import com.meexora.common.dto.EventDetailsDto;
import com.meexora.common.exception.NotFoundException;
import com.meexora.common.kafka.EventCreatedMessage;
import com.meexora.common.kafka.EventEditedMessage;
import com.meexora.eventservice.dto.request.CreateEventRequest;
import com.meexora.eventservice.dto.response.EventEditResponse;
import com.meexora.eventservice.dto.response.EventResponse;
import com.meexora.eventservice.dto.response.EventShortResponse;
import com.meexora.eventservice.kafka.EventCreatedProducer;
import com.meexora.eventservice.kafka.EventEditedProducer;
import com.meexora.eventservice.mapper.EventDetailsMapper;
import com.meexora.eventservice.mapper.EventDtoMapper;
import com.meexora.eventservice.mapper.EventShortResponseMapper;
import com.meexora.eventservice.model.Event;
import com.meexora.eventservice.model.category.EventCategory;
import com.meexora.eventservice.repository.EventRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final EventCreatedProducer eventCreatedProducer;
    private final EventDetailsMapper eventDetailsMapper;
    private final EventShortResponseMapper eventShortResponseMapper;
    private final EventEditedProducer eventEditedProducer;
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

    public Page<EventShortResponse> findEventsByCity(String city, EventCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").ascending());
        OffsetDateTime now = OffsetDateTime.now();

        Page<Event> events;
        if (category == null) {
            events = eventRepository.findByCityIgnoreCaseAndDateAfter(city, now, pageable);
        } else {
            events = eventRepository.findByCityIgnoreCaseAndCategoryAndDateAfter(city, category, now, pageable);
        }

        return events.map(eventShortResponseMapper::toEventShortResponse);
    }



    public Page<EventShortResponse> findEventsNearby(double latitude, double longitude, EventCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").ascending());

        double distance = 3.0;

        Page<Event> events;
        if (category == null) {
            events = eventRepository.findNearby(latitude, longitude, distance, pageable);
        } else {
            events = eventRepository.findNearbyByCategory(latitude, longitude, distance, category.name(), pageable);
        }

        return events.map(eventShortResponseMapper::toEventShortResponse);
    }

    public EventEditResponse getManagementEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                ()-> new NotFoundException("Event not found"));
        return eventDtoMapper.toEditResponse(event);

    }

    public EventResponse updateEvent(@Valid EventEditResponse editEvent, UUID creatorId) {
        Event event = eventRepository.findById(editEvent.getId())
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getCreatorId().equals(creatorId)) {
            throw new IllegalArgumentException("You are not the owner of this event");
        }

        boolean locationChanged = !Objects.equals(event.getLatitude(), editEvent.getLatitude()) ||
                !Objects.equals(event.getLongitude(), editEvent.getLongitude()) ||
                !Objects.equals(event.getAddress(), editEvent.getAddress());

        boolean dateTimeChanged = !Objects.equals(event.getDate(), editEvent.getDate());


        event.setTitle(editEvent.getTitle());
        event.setCategory(editEvent.getCategory());
        event.setDescription(editEvent.getDescription());
        event.setDate(editEvent.getDate());
        event.setLatitude(editEvent.getLatitude());
        event.setLongitude(editEvent.getLongitude());
        event.setAddress(editEvent.getAddress());
        event.setCity(editEvent.getCity());
        event.setPrice(editEvent.getPrice());
        event.setDynamicPricingEnabled(editEvent.getDynamicPricingEnabled());
        event.setMinPrice(editEvent.getMinPrice());
        event.setMaxPrice(editEvent.getMaxPrice());
        Integer addTickets = editEvent.getAddTickets();
        if (addTickets != null && addTickets > 0) {
            event.setTotalTickets(event.getTotalTickets() + addTickets);
        }
        Event updatedEvent = eventRepository.save(event);

        EventEditedMessage message = new EventEditedMessage(
                event.getId(),
                addTickets,
                locationChanged,
                event.getAddress(),
                dateTimeChanged,
                event.getDate().toString()
        );

        eventEditedProducer.sendEventEdited(message);
        System.out.println("Successfully updated event");
        return eventDtoMapper.toResponse(updatedEvent);
    }

}
