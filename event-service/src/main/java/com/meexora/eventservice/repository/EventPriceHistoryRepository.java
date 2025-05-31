package com.meexora.eventservice.repository;

import com.meexora.eventservice.model.EventPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventPriceHistoryRepository extends JpaRepository<EventPriceHistory, UUID> {

    Optional<EventPriceHistory> findFirstByEventIdOrderByCalculatedAtDesc(UUID eventId);
    List<EventPriceHistory> findAllByEventIdOrderByCalculatedAtAsc(UUID eventId);
}
