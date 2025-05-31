package com.meexora.bookingservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
public class CreateBookingRequest {

    @NotNull
    private UUID eventId;

    @NotEmpty
    private List<TicketRequest> tickets;
}
