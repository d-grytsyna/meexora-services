package com.meexora.bookingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingTempService {

    private final StringRedisTemplate redisTemplate;
    private static final Duration BOOKING_EXPIRATION = Duration.ofMinutes(15);

    public void saveBookingExpiration(UUID bookingId) {
        redisTemplate.opsForValue().set(buildKey(bookingId), bookingId.toString(), BOOKING_EXPIRATION);
    }

    public boolean isBookingActive(UUID bookingId) {
        return redisTemplate.hasKey(buildKey(bookingId));
    }

    public void deleteBooking(UUID bookingId) {
        redisTemplate.delete(buildKey(bookingId));
    }

    private String buildKey(UUID bookingId) {
        return "booking:expires:" + bookingId;
    }
}
