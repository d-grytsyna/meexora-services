package com.meexora.paymentservice.service;

import com.meexora.common.exception.NotFoundException;
import com.meexora.common.kafka.BookingCreatedMessage;
import com.meexora.common.kafka.PaymentCompletedMessage;
import com.meexora.paymentservice.kafka.PaymentCompletedProducer;
import com.meexora.paymentservice.model.Payment;
import com.meexora.paymentservice.model.status.PaymentStatus;
import com.meexora.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCompletedProducer paymentCompletedProducer;

    public void createPaymentFromBooking(BookingCreatedMessage bookingCreatedMessage) {
        Payment payment = Payment.builder()
                .bookingId(bookingCreatedMessage.getBookingId())
                .userId(bookingCreatedMessage.getUserId())
                .eventId(bookingCreatedMessage.getEventId())
                .amount(bookingCreatedMessage.getTotalPrice())
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
    }

    public void sendPaymentCompleted(String paymentId) {
        Payment payment = paymentRepository.findById(UUID.fromString(paymentId)).orElseThrow(
                () -> new NotFoundException("Payment not found"));
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);
        PaymentCompletedMessage paymentCompletedMessage =  PaymentCompletedMessage.builder()
                .bookingId(payment.getBookingId())
                .status(payment.getStatus().name()).build();
        paymentCompletedProducer.sendPaymentCompleted(paymentCompletedMessage);
    }
}
