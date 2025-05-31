package com.meexora.eventservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_price_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "new_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal newPrice;

    @Column(name = "calculated_at", nullable = false)
    @CreationTimestamp
    private OffsetDateTime calculatedAt;

    @Column(name = "expected_progress", nullable = false, precision = 5, scale = 4)
    private BigDecimal expectedProgress;

    @Column(name = "actual_progress", nullable = false, precision = 5, scale = 4)
    private BigDecimal actualProgress;

    @Column(name = "reason", nullable = false)
    private String reason;

}
