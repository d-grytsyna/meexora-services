package com.meexora.paymentservice.controller;

import com.meexora.common.dto.PaymentIntentRequest;
import com.meexora.common.dto.PaymentIntentResponse;
import com.meexora.common.response.ApiResponse;
import com.meexora.paymentservice.service.PaymentService;
import com.meexora.paymentservice.service.StripeWebhookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final StripeWebhookService stripeWebhookService;
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) throws IOException {
        String payload = request.getReader().lines().collect(Collectors.joining("\n"));
        String sigHeader = request.getHeader("Stripe-Signature");
        try {
            stripeWebhookService.processWebhook(payload, sigHeader, webhookSecret);
            return ResponseEntity.ok("Event received");
        } catch (SecurityException e) {
            return ResponseEntity.status(400).body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Webhook processing failed");
        }
    }


    @PostMapping("/intent")
    public ResponseEntity<ApiResponse<PaymentIntentResponse>> createPaymentIntent(@RequestBody PaymentIntentRequest request) {
        PaymentIntentResponse response = paymentService.createPaymentFromBooking(request);
        return ResponseEntity.ok(ApiResponse.success("Payment intent: ", response));
    }

    @GetMapping("/intent")
    public ResponseEntity<ApiResponse<PaymentIntentResponse>> getPaymentIntent(
            @RequestParam("bookingId") UUID bookingId) {
        PaymentIntentResponse intent = paymentService.getExistingPaymentIntent(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Retrieved existing intent", intent));
    }

}
