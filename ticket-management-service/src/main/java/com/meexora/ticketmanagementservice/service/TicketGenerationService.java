package com.meexora.ticketmanagementservice.service;


import com.meexora.common.dto.EmailTicketDto;
import com.meexora.common.kafka.TicketEmailMessage;
import com.meexora.common.kafka.TicketGenerationMessage;
import com.meexora.ticketmanagementservice.dto.IssuedTicketPayload;
import com.meexora.ticketmanagementservice.kafka.TicketEmailProducer;
import com.meexora.ticketmanagementservice.model.IssuedTicket;
import com.meexora.ticketmanagementservice.model.status.IssuedTicketStatus;
import com.meexora.ticketmanagementservice.repository.IssuedTicketRepository;
import com.meexora.ticketmanagementservice.utils.QrCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketGenerationService {
    private final IssuedTicketRepository ticketRepository;
    private final QrCodeGenerator qrCodeGenerator;
    private final TicketEmailProducer ticketEmailProducer;

    public void handleTicketGeneration(TicketGenerationMessage message) {
        List<IssuedTicket> issuedTickets = message.getTickets().stream()
                .map(ticket -> {
                    IssuedTicketPayload payload =  IssuedTicketPayload.builder()
                            .ticketId(ticket.getTicketId())
                            .bookingId(message.getBookingId())
                            .eventId(message.getEventId())
                            .userId(message.getUserId())
                            .userName(ticket.getUserName())
                            .build();
                    String qrCode = qrCodeGenerator.generateSignedQrCode(payload);

                    return IssuedTicket.builder()
                            .ticketId(ticket.getTicketId())
                            .bookingId(message.getBookingId())
                            .eventId(message.getEventId())
                            .userId(message.getUserId())
                            .userEmail(message.getUserEmail())
                            .userName(ticket.getUserName())
                            .eventTitle(message.getEventTitle())
                            .eventLocation(message.getEventAddress())
                            .eventDate(message.getEventDate())
                            .price(ticket.getPrice())
                            .qrCode(qrCode)
                            .status(IssuedTicketStatus.ISSUED)
                            .build();
                }).toList();

        ticketRepository.saveAll(issuedTickets);

        List<EmailTicketDto> emailTickets = issuedTickets.stream()
                .map(ticket -> EmailTicketDto.builder()
                        .userName(ticket.getUserName())
                        .eventTitle(ticket.getEventTitle())
                        .eventLocation(ticket.getEventLocation())
                        .eventDate(ticket.getEventDate())
                        .price(ticket.getPrice())
                        .qrCode(ticket.getQrCode())
                        .build())
                .toList();

        TicketEmailMessage emailMessage = TicketEmailMessage.builder()
                .bookingId(message.getBookingId())
                .userEmail(message.getUserEmail())
                .tickets(emailTickets)
                .build();

        ticketEmailProducer.sendTicketEmailEvent(emailMessage);

    }


}
