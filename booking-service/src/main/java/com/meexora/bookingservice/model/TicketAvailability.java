package com.meexora.bookingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ticket_availability")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketAvailability {

    @Id
    private UUID eventId;

    @Column(nullable = false)
    private Integer remainingTickets;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
