package com.meexora.eventservice.model;



import com.meexora.eventservice.model.category.EventCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID creatorId;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;


    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private OffsetDateTime date;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 500)
    private String address;

    @Column(length = 500)
    private String city;

    @Column(nullable = false)
    private Integer totalTickets;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean dynamicPricingEnabled;

    @Column(precision = 10, scale = 2)
    private BigDecimal minPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxPrice;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
