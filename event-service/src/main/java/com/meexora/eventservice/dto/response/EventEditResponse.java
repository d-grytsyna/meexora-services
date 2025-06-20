package com.meexora.eventservice.dto.response;

import com.meexora.eventservice.model.category.EventCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class EventEditResponse {

    private UUID id;

    private UUID creatorId;

    private String title;

    private EventCategory category;

    private String description;

    private OffsetDateTime date;

    private Double latitude;

    private Double longitude;

    private String address;

    private String city;

    private Integer totalTickets;

    private Integer addTickets = 0;

    private BigDecimal price;

    private Boolean dynamicPricingEnabled;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

}
