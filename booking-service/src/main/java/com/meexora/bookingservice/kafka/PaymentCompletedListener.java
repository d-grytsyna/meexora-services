package com.meexora.bookingservice.kafka;

import com.meexora.bookingservice.service.BookingService;
import com.meexora.common.kafka.PaymentCompletedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCompletedListener {

    private final BookingService bookingService;

    @KafkaListener(
            topics = "${kafka.topic.payment-completed}",
            groupId = "booking-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentCompleted(PaymentCompletedMessage message) {
        bookingService.handlePaymentStatusUpdate(message);
    }
}
