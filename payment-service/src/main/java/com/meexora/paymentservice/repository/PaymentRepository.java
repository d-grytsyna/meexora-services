package com.meexora.paymentservice.repository;

import com.meexora.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByBookingId(UUID bookingId);

    Optional<Payment> findPaymentByPaymentIntentId(String paymentId);

    Optional<Payment> findPaymentByBookingId(UUID bookingId);
}
