package com.meexora.bookingservice.repository;

import com.meexora.bookingservice.model.TicketAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketAvailabilityRepository extends JpaRepository<TicketAvailability, UUID> {

    Optional<TicketAvailability> findByEventId(UUID bookingId);
}
