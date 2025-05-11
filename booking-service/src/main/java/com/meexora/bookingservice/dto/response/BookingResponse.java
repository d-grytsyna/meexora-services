package com.meexora.bookingservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meexora.common.dto.PaymentIntentResponse;
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
public class BookingResponse {

    private UUID id;

    private UUID eventId;
    private String eventTitle;
    private String eventLocation;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime eventDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime expiresAt;
    private BigDecimal totalPrice;
    private String status;

    private PaymentIntentResponse paymentIntent;
    private List<TicketDto> tickets;
}
