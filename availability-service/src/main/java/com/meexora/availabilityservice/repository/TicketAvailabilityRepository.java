package com.meexora.availabilityservice.repository;

import com.meexora.availabilityservice.model.TicketAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketAvailabilityRepository extends JpaRepository<TicketAvailability, UUID> {
}
