package com.meexora.paymentservice.service;


import com.meexora.common.exception.NotFoundException;
import com.meexora.paymentservice.dto.StripeAccountStatusResponse;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.meexora.paymentservice.model.StripeAccount;
import com.meexora.paymentservice.repository.StripeAccountRepository;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StripeAccountService {

    private final StripeAccountRepository stripeAccountRepository;
    @Transactional
    public String createIfNotExists(UUID userId, String email, String country) {
        Optional<StripeAccount> existing = stripeAccountRepository.findByUserId(userId);
        if (existing.isPresent()) {
            return existing.get().getStripeAccountId();
        }

        try {
            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setEmail(email)
                    .setCountry(country)
                    .build();

            Account account = Account.create(params);

            StripeAccount saved = StripeAccount.builder()
                    .userId(userId)
                    .stripeAccountId(account.getId())
                    .build();

            stripeAccountRepository.save(saved);

            return account.getId();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Stripe account", e);
        }
    }

    public String generateOnboardingLink(String stripeAccountId, String returnUrl, String refreshUrl) {
        try {
            AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                    .setAccount(stripeAccountId)
                    .setReturnUrl(returnUrl)
                    .setRefreshUrl(refreshUrl)
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build();

            AccountLink accountLink = AccountLink.create(params);

            return accountLink.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Stripe onboarding link", e);
        }
    }
    public Optional<StripeAccount> findByUserId(UUID userId) {
        return stripeAccountRepository.findByUserId(userId);
    }

    public StripeAccountStatusResponse getStripeAccountStatus(UUID userId) {
        StripeAccount stripeAccount = stripeAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Stripe account not found"));

        try {
            Account account = Account.retrieve(stripeAccount.getStripeAccountId());

            return new StripeAccountStatusResponse(
                    account.getChargesEnabled(),
                    account.getPayoutsEnabled(),
                    account.getDetailsSubmitted()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve Stripe account status", e);
        }
    }

    public String getStripeAccountIdByUserId(UUID organizerId) {
        return stripeAccountRepository.findByUserId(organizerId)
                .map(StripeAccount::getStripeAccountId)
                .orElseThrow(() -> new NotFoundException("Stripe account not found for organizer ID: " + organizerId));
    }

    public PaymentIntentCreateParams buildPaymentIntentParams(UUID organizerId, BigDecimal amount) {
        String stripeAccountId = getStripeAccountIdByUserId(organizerId);
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
        return PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("eur")
                .setTransferData(
                        PaymentIntentCreateParams.TransferData.builder()
                                .setDestination(stripeAccountId)
                                .build()
                )
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
    }


}
