package com.meexora.paymentservice.controller;

import com.meexora.common.response.ApiResponse;
import com.meexora.paymentservice.dto.StripeAccountStatusResponse;
import com.meexora.paymentservice.model.StripeAccount;
import com.meexora.paymentservice.service.StripeAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments/stripe/accounts")
@RequiredArgsConstructor
public class StripeAccountController {

    private final StripeAccountService stripeAccountService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createStripeAccount(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Email") String email
    ) {
        String stripeAccountId = stripeAccountService.createIfNotExists(UUID.fromString(userId), email, "ES");
        return ResponseEntity.ok(ApiResponse.success( "Account link: ",stripeAccountId));
    }

    @GetMapping("/onboarding-link")
    public ResponseEntity<ApiResponse<String>> getOnboardingLink(
            @RequestHeader("X-User-Id") String userId
    ) {
        UUID uid = UUID.fromString(userId);

        String stripeAccountId = stripeAccountService.findByUserId(uid)
                .map(StripeAccount::getStripeAccountId)
                .orElseThrow(() -> new IllegalStateException("Stripe account not found for user"));

        String returnUrl = "https://example.com/onboarding/complete";
        String refreshUrl = "https://example.com/onboarding/retry";


        String onboardingLink = stripeAccountService.generateOnboardingLink(stripeAccountId, returnUrl, refreshUrl);
        return ResponseEntity.ok(ApiResponse.success("Onboarding link: " ,onboardingLink));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<StripeAccountStatusResponse>> getStripeAccountStatus(
            @RequestHeader("X-User-Id") String userId
    ) {
        UUID uid = UUID.fromString(userId);
        StripeAccountStatusResponse status = stripeAccountService.getStripeAccountStatus(uid);
        return ResponseEntity.ok(ApiResponse.success( "Status of account received: ",status));
    }


}
