package com.meexora.paymentservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stripe_accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id")
})
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StripeAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "stripe_account_id", nullable = false)
    private String stripeAccountId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
