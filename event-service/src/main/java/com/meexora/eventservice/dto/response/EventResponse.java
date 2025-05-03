package com.meexora.eventservice.dto.response;


import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventResponse {

    private UUID id;

    private UUID creatorId;

    private String title;

    private String description;

    private LocalDateTime date;

    private Double latitude;

    private Double longitude;

    private String address;

    private Integer totalTickets;

    private BigDecimal price;

    private Boolean dynamicPricingEnabled;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Instant createdAt;

    private Instant updatedAt;
}
