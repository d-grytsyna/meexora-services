package com.meexora.bookingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingExpirationListener implements MessageListener {

    private final BookingWatchingService bookingWatchingService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith("booking:expires:")) {
            String bookingIdStr = expiredKey.replace("booking:expires:", "");
            UUID bookingId = UUID.fromString(bookingIdStr);
            bookingWatchingService.handleBookingExpiration(bookingId);

        }
    }
}
