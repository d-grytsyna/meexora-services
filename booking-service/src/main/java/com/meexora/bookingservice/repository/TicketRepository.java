package com.meexora.bookingservice.repository;


import com.meexora.bookingservice.model.Ticket;
import com.meexora.bookingservice.model.status.TicketStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    @Query("""
    SELECT COUNT(t)
    FROM Ticket t
    WHERE t.booking.eventId = :eventId
      AND t.booking.status IN ('PAID', 'RESERVED')
""")
    int countTicketsByEventId(@Param("eventId") UUID eventId);

    @Query("""
    SELECT COUNT(t)
    FROM Ticket t
    WHERE t.booking.eventId = :eventId
      AND t.booking.status IN ('PAID', 'RESERVED')
      AND t.booking.createdAt > :since
""")
    int countTicketsByEventIdSince(@Param("eventId") UUID eventId, @Param("since") OffsetDateTime since);

    List<Ticket> findAllByBookingEventIdAndStatus(UUID eventId, TicketStatus status);

    List<Ticket> findByBookingId(UUID bookingId);
}

