package com.meexora.ticketmanagementservice.service;

import com.meexora.common.exception.NotFoundException;
import com.meexora.ticketmanagementservice.dto.IssuedTicketPayload;
import com.meexora.ticketmanagementservice.dto.request.TicketValidationRequest;
import com.meexora.ticketmanagementservice.dto.response.VerifiedTicketResponse;
import com.meexora.ticketmanagementservice.model.IssuedTicket;
import com.meexora.ticketmanagementservice.model.status.IssuedTicketStatus;
import com.meexora.ticketmanagementservice.repository.IssuedTicketRepository;
import com.meexora.ticketmanagementservice.utils.QrCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketVerificationService {
    private final IssuedTicketRepository ticketRepository;
    private final QrCodeGenerator qrCodeGenerator;

    public VerifiedTicketResponse verifyTicket( TicketValidationRequest ticket) {
        IssuedTicketPayload issuedTicketPayload = qrCodeGenerator.verifyQrCode(ticket.getQrCodeToken());
        if(!issuedTicketPayload.getEventId().equals(ticket.getEventId())) {
            throw new IllegalArgumentException("Ticket doesn't belong to this event");
        }
        IssuedTicket issuedTicket = ticketRepository.findByTicketId(issuedTicketPayload.getTicketId()).orElseThrow(
                () -> new NotFoundException("Ticket not found")
        );
        if(issuedTicket.getStatus()==IssuedTicketStatus.VALIDATED){
            throw new IllegalArgumentException("Ticket is already validated");
        }
        System.out.println("Ticket verified");
        issuedTicket.setStatus(IssuedTicketStatus.VALIDATED);
        ticketRepository.save(issuedTicket);
        return VerifiedTicketResponse.builder()
                .userName(issuedTicket.getUserName())
                .build();
    }
}
