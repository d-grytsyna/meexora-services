package com.meexora.eventservice.controller;

import com.meexora.common.dto.EventDetailsDto;
import com.meexora.common.response.ApiResponse;
import com.meexora.eventservice.dto.request.CreateEventRequest;
import com.meexora.eventservice.dto.response.EventResponse;
import com.meexora.eventservice.dto.response.EventShortResponse;
import com.meexora.eventservice.dto.response.PaginatedResponse;
import com.meexora.eventservice.model.category.EventCategory;
import com.meexora.eventservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
            EventDetailsDto dto = eventService.getEventDetails(id);
            return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/created")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByUser(@RequestHeader("X-User-Id") UUID userId) {
        List<EventResponse> events = eventService.getEventsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Your events: ", events));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventShortResponse>>> getEventsByCity(
            @RequestParam(name = "city") String city,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "category", required = false) EventCategory category
    ) {
        Page<EventShortResponse> pageResult = eventService.findEventsByCity(city, category, page, size);
        PaginatedResponse<EventShortResponse> response = PaginatedResponse.fromPage(pageResult);
        return ResponseEntity.ok(ApiResponse.success("Events found", response));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventShortResponse>>> getEventsNearby(
            @RequestParam(name = "lat") double lat,
            @RequestParam(name = "lng") double lng,
            @RequestParam(name = "category", required = false) String categoryRaw,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        EventCategory category = null;
        if (categoryRaw != null) {
            category = EventCategory.valueOf(categoryRaw);
        }

        Page<EventShortResponse> pageResult = eventService.findEventsNearby(lat, lng, category, page, size);
        PaginatedResponse<EventShortResponse> response = PaginatedResponse.fromPage(pageResult);
        return ResponseEntity.ok(ApiResponse.success("Nearby events found", response));
    }









}
