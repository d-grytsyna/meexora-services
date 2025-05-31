package com.meexora.bookingservice.service;

import com.meexora.bookingservice.client.EventServiceClient;
import com.meexora.bookingservice.dto.request.CreateBookingRequest;
import com.meexora.bookingservice.dto.response.BookingResponse;
import com.meexora.bookingservice.exception.InsufficientTicketsException;
import com.meexora.bookingservice.kafka.WatchingBookingUpdateProducer;
import com.meexora.bookingservice.mapper.BookingMapper;
import com.meexora.bookingservice.model.Booking;
import com.meexora.bookingservice.model.Ticket;
import com.meexora.bookingservice.model.TicketAvailability;
import com.meexora.bookingservice.model.status.BookingStatus;
import com.meexora.bookingservice.model.status.TicketStatus;
import com.meexora.bookingservice.repository.BookingRepository;
import com.meexora.bookingservice.repository.TicketAvailabilityRepository;
import com.meexora.common.dto.EventDetailsDto;
import com.meexora.common.dto.TicketDto;
import com.meexora.common.exception.NotFoundException;
import com.meexora.common.kafka.TicketGenerationMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingWatchingService {
    private final BookingRepository bookingRepository;
    private final EventServiceClient eventServiceClient;
    private final TicketAvailabilityRepository ticketAvailabilityRepository;
    private final BookingMapper bookingMapper;
    private final BookingTempService bookingTempService;
    private final BookingService bookingService;
    private final WatchingBookingUpdateProducer watchingBookingUpdateProducer;

    public BookingResponse createWatchingBooking(CreateBookingRequest request, String userId, String email) {
        EventDetailsDto event = eventServiceClient.getEventById(request.getEventId());
        TicketAvailability availability = ticketAvailabilityRepository.findByEventId(request.getEventId())
                .orElseThrow(() -> new NotFoundException("Ticket availability not found"));
        int requestedTickets = request.getTickets().size();
        if (availability.getRemainingTickets() >= requestedTickets) {
            return bookingService.createBooking(request, userId, email);
        }
        BigDecimal totalPrice = event.getTicketPrice().multiply(BigDecimal.valueOf(requestedTickets));

        Booking booking = Booking.builder()
                .userId(UUID.fromString(userId))
                .userEmail(email)
                .organizerId(event.getCreatorId())
                .eventId(request.getEventId())
                .eventTitle(event.getTitle())
                .eventLocation(event.getAddress())
                .eventDateTime(event.getDate())
                .totalPrice(totalPrice)
                .status(BookingStatus.WATCHING)
                .expiresAt(null)
                .paymentExpiresAt(null)
                .build();

        List<Ticket> tickets = request.getTickets().stream()
                .map(ticket -> Ticket.builder()
                        .booking(booking)
                        .userName(ticket.getUserName())
                        .price(event.getTicketPrice())
                        .status(TicketStatus.WATCHING)
                        .build())
                .toList();

        booking.setTickets(tickets);
        bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    public void convertWatchingToBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getStatus().equals(BookingStatus.WATCHING)) {
            throw new IllegalStateException("Booking is not in WATCHING state");
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = now.plusMinutes(60);
        OffsetDateTime paymentExpiresAt = now.plusMinutes(60);

        booking.setStatus(BookingStatus.RESERVED);
        booking.getTickets().forEach(ticket -> ticket.setStatus(TicketStatus.RESERVED));
        booking.setExpiresAt(expiresAt);
        booking.setPaymentExpiresAt(paymentExpiresAt);
        bookingRepository.save(booking);

        bookingTempService.saveBookingExpiration(booking.getId(), true);

        bookingService.buildBookingResponseWithPaymentIntent(booking);
    }

    @Transactional
    public void handleBookingExpiration(UUID bookingId) {
        Booking expiredBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (expiredBooking.getStatus().equals(BookingStatus.PAID)) {
            return;
        }
        expiredBooking.setStatus(BookingStatus.EXPIRED);
        expiredBooking.getTickets().forEach(ticket -> ticket.setStatus(TicketStatus.EXPIRED));
        bookingRepository.save(expiredBooking);

        int releasedTickets = expiredBooking.getTickets().size();
        TicketAvailability availability = ticketAvailabilityRepository
                .findByEventId(expiredBooking.getEventId())
                .orElseThrow(() -> new NotFoundException("Ticket availability not found"));

        int ticketDifference = releasedTickets;

        List<Booking> watchingBookings = bookingRepository
                .findAllByEventIdAndStatus(expiredBooking.getEventId(), BookingStatus.WATCHING);

        watchingBookings.sort(Comparator.comparing(Booking::getCreatedAt));

        for (Booking watchingBooking : watchingBookings) {
            int needed = watchingBooking.getTickets().size();

            if (needed <= ticketDifference) {
                ticketDifference -= needed;
                convertWatchingToBooking(watchingBooking.getId());
                List<TicketDto> ticketDtos = watchingBooking.getTickets().stream().map(
                        t-> TicketDto.builder()
                                .ticketId(t.getId())
                                .userName(t.getUserName())
                                .price(t.getPrice())
                                .build()
                ).toList();
                TicketGenerationMessage ticketMessage = TicketGenerationMessage.builder()
                        .bookingId(watchingBooking.getId())
                        .eventId(watchingBooking.getEventId())
                        .userId(watchingBooking.getUserId())
                        .eventTitle(watchingBooking.getEventTitle())
                        .eventAddress(watchingBooking.getEventLocation())
                        .eventDate(watchingBooking.getEventDateTime())
                        .userEmail(watchingBooking.getUserEmail())
                        .tickets(ticketDtos)
                        .status(watchingBooking.getStatus().toString())
                        .totalPrice(watchingBooking.getTotalPrice())
                        .build();
                watchingBookingUpdateProducer.sendWatchingBookingUpdatedEvent(ticketMessage);
                watchingBooking.setStatus(BookingStatus.RESERVED);
                watchingBooking.getTickets().forEach(ticket -> ticket.setStatus(TicketStatus.RESERVED));
                bookingRepository.save(watchingBooking);
            }
        }
        availability.setRemainingTickets(availability.getRemainingTickets() + ticketDifference);
        ticketAvailabilityRepository.save(availability);
    }



}
