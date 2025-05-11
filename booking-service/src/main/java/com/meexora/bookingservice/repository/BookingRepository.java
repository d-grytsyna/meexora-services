package com.meexora.bookingservice.repository;

import com.meexora.bookingservice.model.Booking;
import com.meexora.bookingservice.model.status.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {


    List<Booking> findAllByEventId(UUID eventId);

    List<Booking> findAllByEventIdAndStatus(UUID eventId, BookingStatus bookingStatus);
}
