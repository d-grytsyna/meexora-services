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
    private static final Duration BOOKING_EXPIRATION = Duration.ofMinutes(20);
    private static final Duration WATCHING_BOOKING_EXPIRATION = Duration.ofMinutes(60);

    public void saveBookingExpiration(UUID bookingId, boolean isWatching) {
        if(isWatching)        redisTemplate.opsForValue().set(buildKey(bookingId), bookingId.toString(), WATCHING_BOOKING_EXPIRATION);
        else   redisTemplate.opsForValue().set(buildKey(bookingId), bookingId.toString(), BOOKING_EXPIRATION);

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
