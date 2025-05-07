package com.meexora.bookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {

    private UUID id;

    private UUID eventId;
    private String eventTitle;
    private String eventLocation;
    private OffsetDateTime eventDateTime;

    private BigDecimal totalPrice;
    private String status;

    private List<TicketDto> tickets;
}
