package com.meexora.paymentservice.service;

import com.meexora.common.dto.PaymentIntentRequest;
import com.meexora.common.dto.PaymentIntentResponse;
import com.meexora.common.exception.ExternalServiceException;
import com.meexora.common.kafka.PaymentStatusUpdateMessage;
import com.meexora.paymentservice.kafka.PaymentStatusProducer;
import com.meexora.paymentservice.model.Payment;
import com.meexora.paymentservice.model.status.PaymentStatus;
import com.meexora.paymentservice.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusProducer paymentStatusProducer;
    private final StripeAccountService stripeAccountService;

    public PaymentIntentResponse createPaymentFromBooking(PaymentIntentRequest paymentIntentRequest) {

        // Check if payment intent for this booking id exists
        Optional<Payment> existingPayment = paymentRepository.findByBookingId(paymentIntentRequest.getBookingId());
        if (existingPayment.isPresent()) {
            return new PaymentIntentResponse(existingPayment.get().getClientSecret());
        }

        try {
            // Build the params for payment intent based on an organizer account
            PaymentIntentCreateParams params = stripeAccountService.buildPaymentIntentParams(
                    paymentIntentRequest.getOrganizerId(),
                    paymentIntentRequest.getAmount()
            );

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Save a newly created payment
            Payment payment = Payment.builder()
                    .bookingId(paymentIntentRequest.getBookingId())
                    .userId(paymentIntentRequest.getUserId())
                    .eventId(paymentIntentRequest.getEventId())
                    .organizerId(paymentIntentRequest.getOrganizerId())
                    .amount(paymentIntentRequest.getAmount())
                    .status(PaymentStatus.PENDING)
                    .paymentIntentId(paymentIntent.getId())
                    .clientSecret(paymentIntent.getClientSecret())
                    .expiresAt(paymentIntentRequest.getPaymentExpiresAt())
                    .build();

            paymentRepository.save(payment);

            return new PaymentIntentResponse(payment.getClientSecret());

        } catch (StripeException e) {
            throw new ExternalServiceException("Failed to create Stripe PaymentIntent");
        }
    }



    public void sendPaymentCompleted(String paymentId) {
        Payment payment = paymentRepository.findPaymentByPaymentIntentId(paymentId)
                .orElseThrow(() -> new IllegalStateException("Payment not found"));

        if(OffsetDateTime.now().isAfter(payment.getExpiresAt())){
            try {
                RefundCreateParams refundParams = RefundCreateParams.builder()
                        .setPaymentIntent(payment.getPaymentIntentId())
                        .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                        .build();

                Refund.create(refundParams);
                payment.setStatus(PaymentStatus.REFUNDED);

            } catch (StripeException e) {
                payment.setStatus(PaymentStatus.REFUND_FAILED);
                log.error("Refund failed for paymentIntent {}: {}", payment.getPaymentIntentId(), e.getMessage());

            }
        }else{
            payment.setStatus(PaymentStatus.PAID);
        }
        PaymentStatusUpdateMessage paymentCompletedMessage =  PaymentStatusUpdateMessage.builder()
                .bookingId(payment.getBookingId())
                .status(payment.getStatus().name()).build();
        paymentStatusProducer.sendPaymentStatusUpdate(paymentCompletedMessage);
        paymentRepository.save(payment);

    }


}
