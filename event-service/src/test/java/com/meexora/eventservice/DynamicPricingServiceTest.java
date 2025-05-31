package com.meexora.eventservice;

import com.meexora.common.dto.BookingStatisticsResponse;
import com.meexora.common.dto.EventTicketsStatsDto;
import com.meexora.eventservice.client.BookingServiceClient;
import com.meexora.eventservice.model.Event;
import com.meexora.eventservice.model.EventPriceHistory;
import com.meexora.eventservice.model.category.EventCategory;
import com.meexora.eventservice.repository.EventPriceHistoryRepository;
import com.meexora.eventservice.repository.EventRepository;
import com.meexora.eventservice.service.DynamicPricingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class DynamicPricingServiceTest {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventPriceHistoryRepository priceHistoryRepository;
    @Autowired
    private DynamicPricingService dynamicPricingService;
    @Autowired
    private BookingServiceClient bookingServiceClient;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public BookingServiceClient bookingServiceClient() {
            return Mockito.mock(BookingServiceClient.class);
        }
    }

    private Event createEvent(BigDecimal price, Duration sinceCreated, Duration untilEvent) {
        Event event = Event.builder()
                .title("Test Event")
                .creatorId(UUID.randomUUID())
                .latitude(1.0)
                .longitude(1.0)
                .address("Test address")
                .date(OffsetDateTime.now().plus(untilEvent))
                .totalTickets(100)
                .price(price)
                .minPrice(BigDecimal.valueOf(80))
                .maxPrice(BigDecimal.valueOf(120))
                .dynamicPricingEnabled(true)
                .city("Lviv")
                .category(EventCategory.EDUCATION)
                .build();

        event = eventRepository.save(event);
        event.setCreatedAt(Instant.now().minus(sinceCreated));
        return eventRepository.save(event);
    }

    @BeforeEach
    void cleanup() {
        priceHistoryRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    void shouldIncreasePriceWhenHighSales() {
        Event event = createEvent(BigDecimal.valueOf(100), Duration.ofDays(1), Duration.ofDays(2));
        mockStats(event.getId(), 50, 50);

        dynamicPricingService.updateEventPrices();

        BigDecimal updatedPrice = eventRepository.findById(event.getId()).get().getPrice();
        Assertions.assertTrue(updatedPrice.compareTo(BigDecimal.valueOf(100)) > 0);
    }

    @Test
    void shouldDecreasePriceWhenLowSales() {
        Event event = createEvent(BigDecimal.valueOf(100), Duration.ofDays(1), Duration.ofDays(4));
        mockStats(event.getId(), 99, 1);

        dynamicPricingService.updateEventPrices();

        BigDecimal updatedPrice = eventRepository.findById(event.getId()).get().getPrice();
        Assertions.assertTrue(updatedPrice.compareTo(BigDecimal.valueOf(100)) < 0);
    }

    @Test
    void shouldRespectMinAndMaxPriceLimits() {
        Event event = createEvent(BigDecimal.valueOf(120), Duration.ofDays(2), Duration.ofHours(1));
        mockStats(event.getId(), 0, 100);

        dynamicPricingService.updateEventPrices();

        BigDecimal updatedPrice = eventRepository.findById(event.getId()).get().getPrice();
        Assertions.assertTrue(updatedPrice.compareTo(event.getMaxPrice()) <= 0);
        Assertions.assertTrue(updatedPrice.compareTo(event.getMinPrice()) >= 0);
    }

    @Test
    void shouldRoundPriceCorrectly() {
        Event event = createEvent(BigDecimal.valueOf(100), Duration.ofDays(1), Duration.ofDays(1));
        mockStats(event.getId(), 50, 50);

        dynamicPricingService.updateEventPrices();

        BigDecimal updatedPrice = eventRepository.findById(event.getId()).get().getPrice();
        Assertions.assertEquals(2, updatedPrice.scale());
    }

    @Test
    void shouldRemainStableWithZeroInterval() {
        Event event = createEvent(BigDecimal.valueOf(100), Duration.ofDays(0), Duration.ofDays(2));
        mockStatsWithZeroInterval(event.getId(), 100, 0);

        dynamicPricingService.updateEventPrices();

        BigDecimal updatedPrice = eventRepository.findById(event.getId()).get().getPrice();
        Assertions.assertEquals(0, updatedPrice.compareTo(BigDecimal.valueOf(100.00)));

    }

    @Test
    void shouldCreatePriceHistoryEntryOnChange() {
        Event event = createEvent(BigDecimal.valueOf(100), Duration.ofDays(1), Duration.ofDays(3));
        mockStats(event.getId(), 60, 40);

        dynamicPricingService.updateEventPrices();

        List<EventPriceHistory> history = priceHistoryRepository
                .findAllByEventIdOrderByCalculatedAtAsc(event.getId());
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(
                eventRepository.findById(event.getId()).get().getPrice(),
                history.get(0).getNewPrice()
        );
    }

    private void mockStats(UUID eventId, int ticketsLeft, int soldSinceLastChange) {
        BookingStatisticsResponse response = new BookingStatisticsResponse(List.of(
                new EventTicketsStatsDto(eventId, soldSinceLastChange, ticketsLeft)
        ));
        Mockito.when(bookingServiceClient.getBookingStatistics(Mockito.any()))
                .thenReturn(response);
    }

    private void mockStatsWithZeroInterval(UUID eventId, int ticketsLeft, int soldSinceLastChange) {
        priceHistoryRepository.save(EventPriceHistory.builder()
                .event(eventRepository.findById(eventId).get())
                .newPrice(BigDecimal.valueOf(100))
                .calculatedAt(OffsetDateTime.now())
                .expectedProgress(BigDecimal.ZERO)
                .actualProgress(BigDecimal.ZERO)
                .reason("test-zero-interval")
                .build());

        mockStats(eventId, ticketsLeft, soldSinceLastChange);
    }
}

