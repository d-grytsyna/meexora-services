package com.meexora.ticketservice.model;



import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Зв'язок з подією
    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String eventTitle;

    @Column(nullable = false)
    private String eventLocation;

    @Column(nullable = false)
    private Double eventPrice;

    // Зв'язок з користувачем
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String userName;

    // Статус квитка
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // Метадані
    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
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

    public enum Status {
        RESERVED,
        PAID,
        EXPIRED,
        CANCELLED
    }
}
