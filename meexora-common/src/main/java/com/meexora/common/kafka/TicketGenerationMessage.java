package com.meexora.common.kafka;

import com.meexora.common.dto.TicketDto;
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
@NoArgsConstructor
@AllArgsConstructor
public class TicketGenerationMessage {
    private UUID bookingId;
    private UUID eventId;
    private UUID userId;
    private String eventTitle;
    private String eventAddress;
    private OffsetDateTime eventDate;
    private String userEmail;
    private String status;
    private BigDecimal totalPrice;
    private List<TicketDto> tickets;
}
