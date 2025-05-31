package com.meexora.bookingservice.kafka;

import com.meexora.bookingservice.service.BookingService;
import com.meexora.common.kafka.PaymentStatusUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStatusUpdateListener {

    private final BookingService bookingService;

    @KafkaListener(
            topics = "${kafka.topic.payment-status-update}",
            groupId = "booking-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentCompleted(PaymentStatusUpdateMessage message) {
        bookingService.handlePaymentStatusUpdate(message);
    }
}
