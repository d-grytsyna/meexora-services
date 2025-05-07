package com.meexora.gatewayservice.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meexora.gatewayservice.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.meexora.common.response.ErrorResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.isValid(token)) {
            return unauthorized(exchange, "Invalid or expired token");
        }

        String userId;
        String email = null;
        try {
            userId = jwtUtils.getUserId(token);
            if (path.startsWith("/booking/create")) {
                email = jwtUtils.getEmail(token);
            }
        } catch (Exception e) {
            return unauthorized(exchange, "Failed to extract token information");
        }

        ServerHttpRequest.Builder mutatedRequestBuilder = request.mutate()
                .header("X-User-Id", userId);

        if (email != null) {
            mutatedRequestBuilder.header("X-User-Email", email);
        }

        return chain.filter(exchange.mutate().request(mutatedRequestBuilder.build()).build());
    }

    private boolean isWhitelisted(String path) {
        return path.equals("/auth/login")
                || path.equals("/auth/register")
                || path.equals("/auth/registration/request")
                || path.equals("/auth/registration/confirm")
                || path.equals("/auth/password-reset/request")
                || path.equals("/auth/password-reset/confirm")
                || path.equals("/auth/refresh");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(response);
        } catch (JsonProcessingException e) {
            bytes = ("{\"message\":\"Serialization error\"}").getBytes(StandardCharsets.UTF_8);
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}
