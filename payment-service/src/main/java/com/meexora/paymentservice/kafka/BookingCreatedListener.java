package com.meexora.paymentservice.kafka;


import com.meexora.common.kafka.BookingCreatedMessage;
import com.meexora.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingCreatedListener {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${kafka.topic.booking-created}",
            groupId = "payment-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleBookingCreated(BookingCreatedMessage event) {
        paymentService.createPaymentFromBooking(event);
    }
}
