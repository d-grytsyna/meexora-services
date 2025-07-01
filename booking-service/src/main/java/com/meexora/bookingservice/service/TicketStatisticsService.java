package com.meexora.bookingservice.service;

import com.meexora.bookingservice.dto.response.SalesStatsResponse;
import com.meexora.bookingservice.dto.response.TicketSalesPerDayDto;
import com.meexora.bookingservice.model.Ticket;
import com.meexora.bookingservice.model.TicketAvailability;
import com.meexora.bookingservice.model.status.TicketStatus;
import com.meexora.bookingservice.repository.TicketAvailabilityRepository;
import com.meexora.bookingservice.repository.TicketRepository;
import com.meexora.common.dto.EventTicketsStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class TicketStatisticsService {

    private final TicketRepository ticketRepository;
    private final TicketAvailabilityRepository ticketAvailabilityRepository;

    public List<EventTicketsStatsDto> getStatistics(Map<UUID, OffsetDateTime> lastPriceChangeTimestamps) {
        return lastPriceChangeTimestamps.entrySet().stream()
                .map(entry -> {
                    UUID eventId = entry.getKey();
                    OffsetDateTime since = entry.getValue();

                    int sold = (since != null)
                            ? ticketRepository.countTicketsByEventIdSince(eventId, since)
                            : ticketRepository.countTicketsByEventId(eventId);

                    int totalLeft =  ticketAvailabilityRepository.findByEventId(eventId)
                            .map(TicketAvailability::getRemainingTickets)
                            .orElse(0);

                    return new EventTicketsStatsDto(eventId, sold, totalLeft);
                })
                .toList();
    }

    public SalesStatsResponse getTicketSalesStatistics(UUID eventId) {
        List<Ticket> tickets = ticketRepository.findAllByBookingEventIdAndStatus(eventId, TicketStatus.PAID);

        Map<LocalDate, List<Ticket>> grouped = tickets.stream()
                .collect(Collectors.groupingBy(t -> t.getBooking().getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate()));

        List<TicketSalesPerDayDto> perDay = grouped.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Ticket> dayTickets = entry.getValue();
                    long count = dayTickets.size();
                    BigDecimal total = dayTickets.stream()
                            .map(Ticket::getPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new TicketSalesPerDayDto(date, count, total);
                })
                .sorted(Comparator.comparing(TicketSalesPerDayDto::getDate))
                .toList();

        long totalCount = tickets.size();
        BigDecimal totalSum = tickets.stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SalesStatsResponse(perDay, totalCount, totalSum);
    }

}
