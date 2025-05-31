package com.meexora.bookingservice.controller;

import com.meexora.bookingservice.dto.request.CreateBookingRequest;
import com.meexora.bookingservice.dto.response.BookingResponse;
import com.meexora.bookingservice.service.BookingService;
import com.meexora.bookingservice.service.BookingWatchingService;
import com.meexora.bookingservice.service.TicketAvailabilityService;
import com.meexora.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingWatchingService bookingWatchingService;
    private final TicketAvailabilityService ticketAvailabilityService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@RequestBody @Valid CreateBookingRequest request, @RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Email") String email) {
        BookingResponse booking = bookingService.createBooking(request, userId, email);
        return ResponseEntity.ok(ApiResponse.success("Booking created successfully", booking));
    }

    @PostMapping("/watching")
    public ResponseEntity<ApiResponse<BookingResponse>> createWatchingBooking(@RequestBody @Valid CreateBookingRequest request, @RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Email") String email) {
        BookingResponse booking = bookingWatchingService.createWatchingBooking(request, userId, email);
        return ResponseEntity.ok(ApiResponse.success("Ticket monitoring created successfully", booking));
    }

    @PostMapping("/{id}/payment-intent")
    public ResponseEntity<ApiResponse<BookingResponse>> createPaymentIntentRetry(@PathVariable("id") UUID id) {
        BookingResponse booking = bookingService.createPaymentIntentRetry(id);
        return ResponseEntity.ok(ApiResponse.success("Payment intent retry successful", booking));
    }

    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(@RequestParam("eventId") String eventId) {
        boolean available = ticketAvailabilityService.checkTicketAvailability(eventId);
        return ResponseEntity.ok(ApiResponse.success("There are available tickets", available));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(@RequestHeader("X-User-Id") String userId) {
        List<BookingResponse> myBookings = bookingService.getMyBookings(userId);
        return ResponseEntity.ok(ApiResponse.success("Created bookings", myBookings));
    }
}
