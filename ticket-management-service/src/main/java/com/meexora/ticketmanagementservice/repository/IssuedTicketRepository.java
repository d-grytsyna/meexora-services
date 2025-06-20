package com.meexora.ticketmanagementservice.repository;

import com.meexora.ticketmanagementservice.model.IssuedTicket;
import com.meexora.ticketmanagementservice.model.status.IssuedTicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IssuedTicketRepository extends JpaRepository<IssuedTicket, UUID> {

   Optional<IssuedTicket> findByTicketId(UUID ticketId);

    int countByEventIdAndStatus(UUID eventId, IssuedTicketStatus issuedTicketStatus);
}