package com.meexora.authservice.service;

import com.meexora.authservice.model.User;
import com.meexora.authservice.utils.JwtUtils;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpiration;

    @Qualifier("jwtPrivateKey")
    private final Key privateKey;

    public String generateAccessToken(User user) {
        return buildToken(user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshTokenExpiration);
    }

    private String buildToken(User user, long expirationMillis) {
        Instant now = Instant.now();

        JwtBuilder builder = Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMillis)));


        return builder
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
    public boolean isValid(String token) {
        return JwtUtils.isValid(token, privateKey);
    }

    public String getSubject(String token) {
        return JwtUtils.getSubject(token, privateKey);
    }

    public Instant getIssuedAt(String token) {
        return JwtUtils.getIssuedAt(token, privateKey);
    }

    public Instant getExpiration(String token) {
        return JwtUtils.getExpiration(token, privateKey);
    }

    public Instant getStatusUpdatedClaim(String token) {
        return JwtUtils.getStatusUpdatedClaim(token, privateKey);
    }
}