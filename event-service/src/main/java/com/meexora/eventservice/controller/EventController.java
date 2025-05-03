package com.meexora.eventservice.controller;

import com.meexora.common.response.ApiResponse;
import com.meexora.eventservice.dto.request.CreateEventRequest;
import com.meexora.eventservice.dto.response.EventResponse;
import com.meexora.eventservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
