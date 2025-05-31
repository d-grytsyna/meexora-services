package com.meexora.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentRequest {
    private UUID bookingId;
    private BigDecimal amount;
    private UUID userId;
    private UUID eventId;
    private UUID organizerId;
    private OffsetDateTime paymentExpiresAt;
}
