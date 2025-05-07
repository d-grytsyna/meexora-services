package com.meexora.ticketmanagementservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meexora.ticketmanagementservice.dto.IssuedTicketPayload;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class QrCodeGenerator {

    @Value("${security.hmac.secret}")
    private String secret;
    private SecretKeySpec hmacKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        this.hmacKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public String generateSignedQrCode(IssuedTicketPayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(hmacKey);
            byte[] signature = mac.doFinal(json.getBytes(StandardCharsets.UTF_8));

            String encodedPayload = Base64.encodeBase64URLSafeString(json.getBytes(StandardCharsets.UTF_8));
            String encodedSignature = Base64.encodeBase64URLSafeString(signature);
            return encodedPayload + "." + encodedSignature;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate signed QR code", e);
        }
    }

    public IssuedTicketPayload verifyQrCode(String qrCode) {
        try {
            String[] parts = qrCode.split("\\.");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid QR code format");
            }

            byte[] payloadBytes = Base64.decodeBase64(parts[0]);
            byte[] receivedSignature = Base64.decodeBase64(parts[1]);

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(hmacKey);
            byte[] expectedSignature = mac.doFinal(payloadBytes);

            if (!MessageDigest.isEqual(receivedSignature, expectedSignature)) {
                throw new IllegalArgumentException("Invalid QR code signature");
            }

            return objectMapper.readValue(payloadBytes, IssuedTicketPayload.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse QR code payload", e);
        } catch (Exception e) {
            throw new IllegalStateException("QR code verification failed", e);
        }
    }
}
