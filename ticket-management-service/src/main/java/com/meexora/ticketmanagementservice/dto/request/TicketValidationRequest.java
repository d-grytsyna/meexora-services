package com.meexora.ticketmanagementservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TicketValidationRequest {
    @NotNull
    private UUID eventId;
    @NotBlank
    private String qrCodeToken;
}
