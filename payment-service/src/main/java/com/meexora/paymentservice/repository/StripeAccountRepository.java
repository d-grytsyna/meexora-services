package com.meexora.paymentservice.repository;

import com.meexora.paymentservice.model.StripeAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StripeAccountRepository extends JpaRepository<StripeAccount, UUID> {

    Optional<StripeAccount> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
