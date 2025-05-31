package com.meexora.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RegistrationTempService {

    private final StringRedisTemplate redisTemplate;
    private static final Duration CODE_EXPIRATION = Duration.ofMinutes(10);
    private static final Duration REQUEST_BLOCK_DURATION = Duration.ofMinutes(1);


    // Registration flow: verification code
    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(buildKey(email), code, CODE_EXPIRATION);
    }

    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get(buildKey(email));
    }

    public void deleteVerificationCode(String email) {
        redisTemplate.delete(buildKey(email));
    }

    private String buildKey(String email) {
        return "registration:" + email;
    }


    // Block too many requests for verification
    public boolean isRequestRegistrationBlocked(String email) {
        return redisTemplate.hasKey(buildRegistrationBlockKey(email));
    }

    public void blockRegistrationRequest(String email) {
        redisTemplate.opsForValue().set(buildRegistrationBlockKey(email), "BLOCKED", REQUEST_BLOCK_DURATION);
    }

    private String buildRegistrationBlockKey(String email) {
        return "block:registration:" + email;
    }


    //Forgot password flow
    public void saveForgotPasswordCode(String email, String code) {
        redisTemplate.opsForValue().set(buildForgotPasswordKey(email), code, Duration.ofMinutes(10));
    }

    public String getForgotPasswordCode(String email) {
        return redisTemplate.opsForValue().get(buildForgotPasswordKey(email));
    }

    public void deleteForgotPasswordCode(String email) {
        redisTemplate.delete(buildForgotPasswordKey(email));
    }

    private String buildForgotPasswordKey(String email) {
        return "forgot-password:" + email;
    }

    // Block too many requests forgot password
    public boolean isRequestForgotPasswordBlocked(String email) {
        return redisTemplate.hasKey(buildForgotPasswordBlockKey(email));
    }

    public void blockForgotPasswordRequest(String email) {
        redisTemplate.opsForValue().set(buildForgotPasswordBlockKey(email), "BLOCKED", REQUEST_BLOCK_DURATION);
    }

    private String buildForgotPasswordBlockKey(String email) {
        return "block:forgot-password:" + email;
    }
}