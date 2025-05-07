package com.meexora.eventservice.dto.request;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEventRequest {

        @NotBlank
        @Size(max = 255)
        private String title;

        @NotBlank
        @Size(max = 10000)
        private String description;

        @NotNull
        private OffsetDateTime date;

        @NotNull
        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        private Double latitude;

        @NotNull
        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        private Double longitude;

        @Size(max = 500)
        private String address;

        @NotNull
        @Min(1)
        private Integer totalTickets;

        @NotNull
        @DecimalMin(value = "0.0")
        private BigDecimal price;

        @NotNull
        private Boolean dynamicPricingEnabled;

        @DecimalMin(value = "0.0")
        private BigDecimal minPrice;

        @DecimalMin(value = "0.0")
        private BigDecimal maxPrice;
}
