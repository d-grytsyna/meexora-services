package com.meexora.bookingservice.client;

import com.meexora.common.dto.EventDetailsDto;
import com.meexora.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceClient {

    private final RestTemplate restTemplate;

    @Value("${client.event-service.url}")
    private String eventServiceUrl;

    public EventDetailsDto getEventById(UUID eventId) {
        String url = eventServiceUrl + "/event/" + eventId;

        try {
            ResponseEntity<ApiResponse<EventDetailsDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<EventDetailsDto>>() {}
            );

            ApiResponse<EventDetailsDto> body = response.getBody();
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
