package com.meexora.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTicketDto {
    private String userName;
    private String eventTitle;
    private String eventLocation;
    private OffsetDateTime eventDate;
    private BigDecimal price;
    private String qrCode;
}
