package com.meexora.ticketmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssuedTicketPayload{
    private UUID ticketId;
    private UUID bookingId;
    private UUID eventId;
    private UUID userId;
    private String userName;
}
