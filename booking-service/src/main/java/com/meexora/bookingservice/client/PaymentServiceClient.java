package com.meexora.bookingservice.client;

import com.meexora.common.dto.PaymentIntentRequest;
import com.meexora.common.dto.PaymentIntentResponse;
import com.meexora.common.exception.ExternalServiceException;
import com.meexora.common.exception.ServiceUnavailableException;
import com.meexora.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${client.payment-service.url}")
    private String paymentServiceUrl;

    public PaymentIntentResponse setPaymentIntent(PaymentIntentRequest paymentIntentRequest) {
        String url = paymentServiceUrl + "/payments/intent";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PaymentIntentRequest> entity = new HttpEntity<>(paymentIntentRequest, headers);

            ResponseEntity<ApiResponse<PaymentIntentResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            ApiResponse<PaymentIntentResponse> body = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && body != null && body.isSuccess()) {
                return body.getData();
            } else {
                throw new IllegalStateException("Failed to create payment intent: " +
                        (body != null ? body.getMessage() : "Empty response"));
            }

        } catch (HttpStatusCodeException e) {
            throw new ExternalServiceException("Payment service error: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Payment service unavailable: " + e.getMessage());
        }
    }
}