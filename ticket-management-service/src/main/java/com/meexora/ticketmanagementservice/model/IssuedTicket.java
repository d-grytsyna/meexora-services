package com.meexora.ticketmanagementservice.model;

import com.meexora.ticketmanagementservice.model.status.IssuedTicketStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "issued_tickets")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IssuedTicket {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "ticket_id", nullable = false)
    private UUID ticketId;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "event_title", nullable = false)
    private String eventTitle;

    @Column(name = "event_location", nullable = false)
    private String eventLocation;

    @Column(name = "event_date", nullable = false)
    private OffsetDateTime eventDate;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "qr_code", nullable = false)
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssuedTicketStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
