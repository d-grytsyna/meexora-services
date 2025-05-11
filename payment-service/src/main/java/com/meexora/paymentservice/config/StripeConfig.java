package com.meexora.paymentservice.config;

import com.stripe.Stripe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig{

    @Bean
    public StripeInitializer stripeInitializer(StripeProperties props) {
        Stripe.apiKey = props.getSecretKey();
        return new StripeInitializer();
    }

    public static class StripeInitializer {}
}
