package com.meexora.bookingservice.controller;

import com.meexora.bookingservice.dto.response.SalesStatsResponse;
import com.meexora.bookingservice.service.TicketStatisticsService;
import com.meexora.common.dto.BookingStatisticsRequest;
import com.meexora.common.dto.BookingStatisticsResponse;
import com.meexora.common.dto.EventTicketsStatsDto;
import com.meexora.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final TicketStatisticsService ticketStatisticsService;

    @GetMapping
    public BookingStatisticsResponse getBookingStatistics(@RequestBody BookingStatisticsRequest request) {
        List<EventTicketsStatsDto> stats = ticketStatisticsService.getStatistics(request.getLastPriceChangeTimestamps());
        return new BookingStatisticsResponse(stats);
    }

    @GetMapping("/{eventId}/sales-per-day")
    public ResponseEntity<ApiResponse<SalesStatsResponse>> getTicketSalesStatistics(@PathVariable("eventId") UUID eventId) {
        SalesStatsResponse stats = ticketStatisticsService.getTicketSalesStatistics(eventId);
        return  ResponseEntity.ok(ApiResponse.success("Stats retrieved", stats));
    }

}
