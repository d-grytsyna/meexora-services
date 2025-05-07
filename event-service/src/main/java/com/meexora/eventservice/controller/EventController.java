package com.meexora.eventservice.controller;

import com.meexora.common.dto.EventDetailsDto;
import com.meexora.common.response.ApiResponse;
import com.meexora.eventservice.dto.request.CreateEventRequest;
import com.meexora.eventservice.dto.response.EventResponse;
import com.meexora.eventservice.dto.response.EventShortResponse;
import com.meexora.eventservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController{
    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@RequestHeader("X-User-Id") UUID creatorId, @Valid @RequestBody CreateEventRequest request) {
        EventResponse createdEvent = eventService.createEvent(request, creatorId);
        return ResponseEntity.ok(ApiResponse.success("Event created successfully" , createdEvent));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDetailsDto>> getEventById(@PathVariable("id") UUID id) {
        System.out.println("Event service get event by id: " + id);
            EventDetailsDto dto = eventService.getEventDetails(id);
        System.out.println(dto.getTicketPrice());
            return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/created")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByUser(@RequestHeader("X-User-Id") UUID userId) {
        List<EventResponse> events = eventService.getEventsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Your events: ", events));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<EventShortResponse>>> getAllEvents() {
        List<EventShortResponse> eventList = eventService.findAllEvents();

        System.out.println(eventList.size());
        return ResponseEntity.ok(ApiResponse.success("All events found: ", eventList));
    }




}
