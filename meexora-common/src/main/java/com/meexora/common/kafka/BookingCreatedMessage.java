package com.meexora.common.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedMessage {
    private UUID bookingId;
    private UUID eventId;
    private BigDecimal totalPrice;
    private UUID userId;
}
