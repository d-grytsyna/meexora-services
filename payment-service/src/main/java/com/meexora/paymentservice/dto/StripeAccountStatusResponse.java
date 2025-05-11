package com.meexora.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StripeAccountStatusResponse {
    private boolean chargesEnabled;
    private boolean payoutsEnabled;
    private boolean detailsSubmitted;
}
