package com.meexora.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailsDto {
    private UUID id;
    private String title;
    private String address;
    private OffsetDateTime date;
    private BigDecimal ticketPrice;
}
