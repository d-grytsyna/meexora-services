package com.meexora.eventservice.service;

import com.meexora.common.dto.BookingStatisticsRequest;
import com.meexora.common.dto.BookingStatisticsResponse;
import com.meexora.common.dto.EventTicketsStatsDto;
import com.meexora.eventservice.client.BookingServiceClient;
import com.meexora.eventservice.model.Event;
import com.meexora.eventservice.model.EventPriceHistory;
import com.meexora.eventservice.repository.EventPriceHistoryRepository;
import com.meexora.eventservice.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DynamicPricingService {
    private final EventRepository eventRepository;
    private final EventPriceHistoryRepository priceHistoryRepository;
    private final BookingServiceClient bookingServiceClient;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateEventPrices() {
        List<Event> events = eventRepository.findAllByDynamicPricingEnabledIsTrueAndDateAfter(OffsetDateTime.now());

        Map<UUID, OffsetDateTime> lastChangeTimestamps = new HashMap<>();
        for (Event event : events) {
            OffsetDateTime lastChange = priceHistoryRepository
                    .findFirstByEventIdOrderByCalculatedAtDesc(event.getId())
                    .map(EventPriceHistory::getCalculatedAt)
                    .orElse(null);

            lastChangeTimestamps.put(event.getId(), lastChange);
        }

        BookingStatisticsRequest request = new BookingStatisticsRequest(lastChangeTimestamps);

        BookingStatisticsResponse response = bookingServiceClient.getBookingStatistics(request);

        for (EventTicketsStatsDto stat : response.getEventTicketsStats()) {
            UUID eventId = stat.eventId();

            Optional<Event> eventOpt = events.stream()
                    .filter(e -> e.getId().equals(eventId))
                    .findFirst();

            if (eventOpt.isEmpty()) continue;

            Event event = eventOpt.get();


            // Progress % in number of total sold tickets
            int currentlySold = event.getTotalTickets() - stat.totalTicketsLeft();
            BigDecimal actualProgress = BigDecimal.valueOf((double) currentlySold / event.getTotalTickets())
                    .setScale(4, RoundingMode.HALF_UP);

            // Progress % in total time
            long totalHours = Duration.between(event.getCreatedAt(), event.getDate()).toHours();
            long passedHours = Duration.between(event.getCreatedAt(), Instant.now()).toHours();
            BigDecimal expectedProgress = (totalHours == 0)
                    ? BigDecimal.ONE
                    : BigDecimal.valueOf((double) passedHours / totalHours)
                    .setScale(4, RoundingMode.HALF_UP);

            // Progress in an interval from last price change
            int soldSinceLastChange = stat.soldTickets();
            OffsetDateTime lastChangeAt = lastChangeTimestamps.get(eventId);
            long intervalHours = (lastChangeAt != null)
                    ? Duration.between(lastChangeAt, OffsetDateTime.now()).toHours()
                    : Duration.between(event.getCreatedAt(), OffsetDateTime.now()).toHours();

            double actualLocalRate = (intervalHours == 0)
                    ? 0
                    : (double) soldSinceLastChange / intervalHours;

            double expectedRate = (totalHours == 0)
                    ? 0
                    : (double) event.getTotalTickets() / totalHours;

            // Difference between sold tickets and time progress
            // Improvement in an interval
            BigDecimal tempoDelta = (intervalHours == 0 || expectedRate == 0)
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(actualLocalRate / expectedRate - 1)
                    .setScale(4, RoundingMode.HALF_UP);

            // Improvement in total
            BigDecimal delta = actualProgress.subtract(expectedProgress);

            // Improvement of sales that includes improvement in an interval and in total
            BigDecimal effectiveDelta = delta.multiply(BigDecimal.valueOf(0.75))
                    .add(tempoDelta.multiply(BigDecimal.valueOf(0.25)));

            BigDecimal adjustment = effectiveDelta
                    .multiply(event.getMaxPrice().subtract(event.getMinPrice()))
                    .setScale(2, RoundingMode.HALF_UP);


            BigDecimal previousPrice = event.getPrice();

            BigDecimal newPrice = previousPrice.add(adjustment)
                    .max(event.getMinPrice())
                    .min(event.getMaxPrice())
                    .setScale(0, RoundingMode.HALF_UP);

            if (newPrice.compareTo(previousPrice) != 0) {
                event.setPrice(newPrice);
                priceHistoryRepository.save(EventPriceHistory.builder()
                        .event(event)
                        .newPrice(newPrice)
                        .calculatedAt(OffsetDateTime.now())
                        .expectedProgress(expectedProgress)
                        .actualProgress(actualProgress)
                        .reason("scheduled")
                        .build());
            }
        }
    }
}
