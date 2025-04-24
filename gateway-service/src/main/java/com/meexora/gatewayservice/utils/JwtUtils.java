package com.meexora.gatewayservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtUtils {


    @Qualifier("jwtPublicKey")
    private final PublicKey publicKey;

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    public Instant getIssuedAt(String token) {
        return getClaims(token).getIssuedAt().toInstant();
    }
}
