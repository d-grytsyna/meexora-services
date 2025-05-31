package com.meexora.authservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.time.Instant;
import java.util.function.Function;

public class JwtUtils {

    public static Claims getAllClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean isValid(String token, Key key) {
        try {
            getAllClaims(token, key);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static <T> T extractClaim(String token, Key key, Function<Claims, T> resolver) {
        return resolver.apply(getAllClaims(token, key));
    }

    public static String getSubject(String token, Key key) {
        return extractClaim(token, key, Claims::getSubject);
    }

    public static Instant getIssuedAt(String token, Key key) {
        return extractClaim(token, key, Claims::getIssuedAt).toInstant();
    }

    public static Instant getExpiration(String token, Key key) {
        return extractClaim(token, key, Claims::getExpiration).toInstant();
    }

    public static Instant getStatusUpdatedClaim(String token, Key key) {
        String value = (String) getAllClaims(token, key).get("status_updated");
        return Instant.parse(value);
    }
}
