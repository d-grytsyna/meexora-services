package com.meexora.paymentservice.controller;

import com.meexora.paymentservice.dto.PaymentWebhookDto;
import com.meexora.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody PaymentWebhookDto webhookDto) {
        paymentService.sendPaymentCompleted(webhookDto.getPaymentId());
        return ResponseEntity.ok().build();
    }

}
