package com.meexora.bookingservice.controller;

import com.meexora.bookingservice.dto.request.CreateBookingRequest;
import com.meexora.bookingservice.dto.response.BookingResponseDto;
import com.meexora.bookingservice.service.BookingService;
import com.meexora.bookingservice.service.TicketAvailabilityService;
import com.meexora.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final TicketAvailabilityService ticketAvailabilityService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<BookingResponseDto>> createBooking(@RequestBody @Valid CreateBookingRequest request, @RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Email") String email) {
        BookingResponseDto booking = bookingService.createBooking(request, userId, email);
        return ResponseEntity.ok(ApiResponse.success("Booking created successfully", booking));
    }

    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(@RequestParam("eventId") String eventId) {
        boolean available = ticketAvailabilityService.checkTicketAvailability(eventId);
        return ResponseEntity.ok(ApiResponse.success("There are available tickets", available));
    }
}
