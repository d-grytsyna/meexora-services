package com.meexora.bookingservice.service;

import com.meexora.bookingservice.client.EventServiceClient;
import com.meexora.bookingservice.dto.request.CreateBookingRequest;
import com.meexora.bookingservice.dto.response.BookingResponseDto;
import com.meexora.bookingservice.kafka.BookingConfirmedProducer;
import com.meexora.bookingservice.kafka.BookingCreatedProducer;
import com.meexora.bookingservice.mapper.BookingMapper;
import com.meexora.bookingservice.model.Booking;
import com.meexora.bookingservice.model.Ticket;
import com.meexora.bookingservice.model.TicketAvailability;
import com.meexora.bookingservice.model.status.BookingStatus;
import com.meexora.bookingservice.model.status.TicketStatus;
import com.meexora.bookingservice.repository.BookingRepository;
import com.meexora.bookingservice.repository.TicketAvailabilityRepository;
import com.meexora.bookingservice.repository.TicketRepository;
import com.meexora.common.dto.EventDetailsDto;
import com.meexora.common.dto.TicketDto;
import com.meexora.common.exception.NotFoundException;
import com.meexora.common.kafka.BookingCreatedMessage;
import com.meexora.common.kafka.PaymentCompletedMessage;
import com.meexora.common.kafka.TicketGenerationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingConfirmedProducer bookingConfirmedProducer;
    private final TicketRepository ticketRepository;
    private final TicketAvailabilityRepository ticketAvailabilityRepository;
    private final EventServiceClient eventServiceClient;
    private final BookingTempService bookingTempService;
    private final BookingMapper bookingMapper;
    private final BookingCreatedProducer bookingCreatedProducer;


    public BookingResponseDto createBooking(CreateBookingRequest request, String userId, String email) {
        EventDetailsDto event = eventServiceClient.getEventById(request.getEventId());
        BigDecimal totalPrice = event.getTicketPrice().multiply(BigDecimal.valueOf(request.getTickets().size()));

        int requestedTickets = request.getTickets().size();
        TicketAvailability availability = ticketAvailabilityRepository.findByEventId(request.getEventId())
                .orElseThrow( () -> new NotFoundException("Ticket availability not found"));

        if(availability.getRemainingTickets()<requestedTickets){
            throw new IllegalStateException("Not enough tickets available for booking");
        }

        availability.setRemainingTickets(availability.getRemainingTickets()-requestedTickets);
        ticketAvailabilityRepository.save(availability);

        Booking booking = Booking.builder()
                .userId(UUID.fromString(userId))
                .userEmail(email)
                .eventId(request.getEventId())
                .eventTitle(event.getTitle())
                .eventLocation(event.getAddress())
                .eventDateTime(event.getDate())
                .totalPrice(totalPrice)
                .status(BookingStatus.RESERVED)
                        .build();

        List<Ticket> tickets = request.getTickets().stream()
                .map(ticket -> Ticket.builder()
                        .booking(booking)
                        .userName(ticket.getUserName())
                        .price(event.getTicketPrice())
                        .status(TicketStatus.RESERVED)
                        .build())
                .toList();

        booking.setTickets(tickets);
        bookingRepository.save(booking);
        bookingTempService.saveBookingExpiration(booking.getId());
        BookingCreatedMessage  bookingCreatedMessage = BookingCreatedMessage.builder()
                .bookingId(booking.getId())
                .eventId(booking.getEventId())
                .userId(booking.getUserId())
                .totalPrice(booking.getTotalPrice())
                .build();
        bookingCreatedProducer.sendBookingCreatedEvent(bookingCreatedMessage);
        return bookingMapper.toDto(booking);
    }

    public void handlePaymentStatusUpdate(PaymentCompletedMessage paymentCompletedMessage) {
        Booking booking = bookingRepository.findById(paymentCompletedMessage.getBookingId()).orElseThrow( () -> new NotFoundException("Booking not found"));
        List<Ticket> tickets = ticketRepository.findByBookingId(paymentCompletedMessage.getBookingId());
        if (tickets.isEmpty()) {
            throw new NotFoundException("Tickets not found for booking: " + paymentCompletedMessage.getBookingId());
        }

        booking.setStatus(BookingStatus.PAID);
        tickets.forEach(ticket -> {
            ticket.setStatus(TicketStatus.PAID);
        });


        bookingRepository.save(booking);
        ticketRepository.saveAll(tickets);

        List<TicketDto> ticketDtos = tickets.stream().map(
                t-> TicketDto.builder()
                        .ticketId(t.getId())
                        .userName(t.getUserName())
                        .price(t.getPrice())
                        .build()
        ).toList();
        TicketGenerationMessage ticketMessage = TicketGenerationMessage.builder()
                .bookingId(booking.getId())
                .eventId(booking.getEventId())
                .userId(booking.getUserId())
                .eventTitle(booking.getEventTitle())
                .eventAddress(booking.getEventLocation())
                .eventDate(booking.getEventDateTime())
                .userEmail(booking.getUserEmail())
                .tickets(ticketDtos)
                .build();
        bookingConfirmedProducer.sendBookingConfirmedEvent(ticketMessage);
    }
}

