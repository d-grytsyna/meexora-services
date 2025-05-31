package com.meexora.paymentservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StripeWebhookService {

    private final PaymentService paymentService;

    public void processWebhook(String payload, String signatureHeader, String webhookSecret) throws IOException {
        Event event = verifySignature(payload, signatureHeader, webhookSecret);

        if ("payment_intent.succeeded".equals(event.getType())) {
            String paymentIntentId = extractPaymentIntentId(event, payload);
            paymentService.sendPaymentCompleted(paymentIntentId);
        }
    }

    private Event verifySignature(String payload, String signatureHeader, String webhookSecret) {
        try {
            return Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (Exception e) {
            throw new SecurityException("Invalid signature");
        }
    }

    private String extractPaymentIntentId(Event event, String payload) throws IOException {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        if (deserializer.getObject().isPresent()) {
            PaymentIntent intent = (PaymentIntent) deserializer.getObject().get();
            return intent.getId();
        } else {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);
            return root.path("data").path("object").path("id").asText();
        }
    }
}
