package com.meexora.eventservice.client;

import com.meexora.common.dto.*;
import com.meexora.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class BookingServiceClient {

    private final RestTemplate restTemplate;

    @Value("${client.booking-service.url}")
    private String bookingServiceUrl;

    public BookingStatisticsResponse getBookingStatistics(BookingStatisticsRequest bookingStatisticsRequest) {
        String url = bookingServiceUrl + "/statistics";

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<BookingStatisticsRequest> entity = new HttpEntity<>(bookingStatisticsRequest, headers);

            ResponseEntity<ApiResponse<BookingStatisticsResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );


            ApiResponse<BookingStatisticsResponse> body = response.getBody();
            if (body != null && body.isSuccess()) {
                return body.getData();
            } else {
                throw new IllegalStateException("Event data not available or invalid response");
            }

        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to fetch event data: " + e.getMessage(), e);
        }
    }
}