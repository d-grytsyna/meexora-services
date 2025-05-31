package com.meexora.eventservice.repository;


import com.meexora.eventservice.model.Event;
import com.meexora.eventservice.model.category.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findAllByCreatorId(UUID creatorId);
    List<Event> findAllByDynamicPricingEnabledIsTrueAndDateAfter(OffsetDateTime time);

    Page<Event> findByCityIgnoreCase(String city, Pageable pageable);
    Page<Event> findByCityIgnoreCaseAndCategory(String city, EventCategory category, Pageable pageable);

    @Query(value = """
    SELECT e.*, 
           (6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * 
           cos(radians(e.longitude) - radians(:longitude)) + 
           sin(radians(:latitude)) * sin(radians(e.latitude)))) AS distance 
    FROM events e 
    WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * 
           cos(radians(e.longitude) - radians(:longitude)) + 
           sin(radians(:latitude)) * sin(radians(e.latitude)))) < :distance
    ORDER BY distance
    """,
            countQuery = """
    SELECT count(*) 
    FROM events e 
    WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * 
           cos(radians(e.longitude) - radians(:longitude)) + 
           sin(radians(:latitude)) * sin(radians(e.latitude)))) < :distance
    """,
            nativeQuery = true)
    Page<Event> findNearby(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") double distance,
            Pageable pageable
    );

    @Query(value = """
    SELECT e.*, 
           (6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * 
           cos(radians(e.longitude) - radians(:longitude)) + 
           sin(radians(:latitude)) * sin(radians(e.latitude)))) AS distance 
    FROM events e 
    WHERE e.category = :category AND
          (6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * 
           cos(radians(e.longitude) - radians(:longitude)) + 
           sin(radians(:latitude)) * sin(radians(e.latitude)))) < :distance
    ORDER BY distance
    """,
            countQuery = """
    SELECT count(*) 
    FROM events e 
    WHERE e.category = :category AND
          (6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * 
           cos(radians(e.longitude) - radians(:longitude)) + 
           sin(radians(:latitude)) * sin(radians(e.latitude)))) < :distance
    """,
            nativeQuery = true)
    Page<Event> findNearbyByCategory(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") double distance,
            @Param("category") String category,
            Pageable pageable
    );

}
