package com.meexora.bookingservice.service;

import com.meexora.bookingservice.client.EventServiceClient;
import com.meexora.bookingservice.client.PaymentServiceClient;
import com.meexora.bookingservice.dto.request.CreateBookingRequest;
import com.meexora.bookingservice.exception.InsufficientTicketsException;
import com.meexora.bookingservice.kafka.BookingConfirmedProducer;
import com.meexora.bookingservice.kafka.BookingRefundedProducer;
import com.meexora.common.dto.PaymentIntentRequest;
import com.meexora.bookingservice.dto.response.BookingResponse;
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
import com.meexora.common.dto.PaymentIntentResponse;
import com.meexora.common.dto.TicketDto;
import com.meexora.common.exception.ExternalServiceException;
import com.meexora.common.exception.NotFoundException;
import com.meexora.common.exception.ServiceUnavailableException;
import com.meexora.common.kafka.PaymentStatusUpdateMessage;
import com.meexora.common.kafka.TicketGenerationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingConfirmedProducer bookingConfirmedProducer;
    private final BookingRefundedProducer bookingRefundedProducer;
    private final TicketRepository ticketRepository;
    private final TicketAvailabilityRepository ticketAvailabilityRepository;
    private final EventServiceClient eventServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final BookingTempService bookingTempService;
    private final BookingMapper bookingMapper;


    public BookingResponse createBooking(CreateBookingRequest request, String userId, String email) {

        // Get the information about the chosen event from the event-service
        EventDetailsDto event = eventServiceClient.getEventById(request.getEventId());
        BigDecimal totalPrice = event.getTicketPrice().multiply(BigDecimal.valueOf(request.getTickets().size()));

        // Check if there are available tickets for this event
        int requestedTickets = request.getTickets().size();
        TicketAvailability availability = ticketAvailabilityRepository.findByEventId(request.getEventId())
                .orElseThrow( () -> new NotFoundException("Ticket availability not found"));
        if(availability.getRemainingTickets()<requestedTickets){
            throw new InsufficientTicketsException("Not enough tickets available for booking");
        }

        // Decrease the number of available tickets
        availability.setRemainingTickets(availability.getRemainingTickets()-requestedTickets);
        ticketAvailabilityRepository.save(availability);

        // Create a new booking with expiration time of 15 minutes
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = now.plusMinutes(15);
        OffsetDateTime paymentExpiresAt = now.plusMinutes(20);
        Booking booking = Booking.builder()
                .userId(UUID.fromString(userId))
                .userEmail(email)
                .organizerId(event.getCreatorId())
                .eventId(request.getEventId())
                .eventTitle(event.getTitle())
                .eventLocation(event.getAddress())
                .eventDateTime(event.getDate())
                .totalPrice(totalPrice)
                .expiresAt(expiresAt)
                .paymentExpiresAt(paymentExpiresAt)
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

        // Save the booking expiration time to redis
        bookingTempService.saveBookingExpiration(booking.getId(), false);

        return buildBookingResponseWithPaymentIntent(booking);
    }

    public BookingResponse createPaymentIntentRetry(UUID bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getStatus().equals(BookingStatus.RESERVED)) {
            throw new IllegalStateException("Payment cannot be retried for this booking status");
        }
        return buildBookingResponseWithPaymentIntent(booking);
    }

    public BookingResponse buildBookingResponseWithPaymentIntent(Booking booking) {
        BookingResponse response = bookingMapper.toDto(booking);

        try {
            PaymentIntentRequest paymentIntentRequest = PaymentIntentRequest.builder()
                    .bookingId(booking.getId())
                    .eventId(booking.getEventId())
                    .userId(booking.getUserId())
                    .organizerId(booking.getOrganizerId())
                    .paymentExpiresAt(booking.getPaymentExpiresAt())
                    .amount(booking.getTotalPrice())
                    .build();

            PaymentIntentResponse paymentIntentResponse = paymentServiceClient.setPaymentIntent(paymentIntentRequest);
            response.setPaymentIntent(paymentIntentResponse);

        } catch (ExternalServiceException | ServiceUnavailableException | IllegalArgumentException e) {
            log.warn("Payment intent was not created for booking {}: {}", booking.getId(), e.getMessage());
        }

        return response;
    }

    public void handlePaymentStatusUpdate(PaymentStatusUpdateMessage paymentStatusUpdateMessage) {
        Booking booking = bookingRepository.findById(paymentStatusUpdateMessage.getBookingId()).orElseThrow( () -> new NotFoundException("Booking not found"));
        List<Ticket> tickets = ticketRepository.findByBookingId(paymentStatusUpdateMessage.getBookingId());
        if (tickets.isEmpty()) {
            throw new NotFoundException("Tickets not found for booking: " + paymentStatusUpdateMessage.getBookingId());
        }

        booking.setStatus(BookingStatus.valueOf(paymentStatusUpdateMessage.getStatus()));
        tickets.forEach(ticket -> {
            ticket.setStatus(TicketStatus.valueOf(paymentStatusUpdateMessage.getStatus()));
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
                .status(booking.getStatus().toString())
                .totalPrice(booking.getTotalPrice())
                .build();
        if(booking.getStatus().equals(BookingStatus.PAID)) {
            bookingConfirmedProducer.sendBookingUpdatedEvent(ticketMessage);
        }else{
            bookingRefundedProducer.sendBookingUpdatedEvent(ticketMessage);
        }
    }
}

