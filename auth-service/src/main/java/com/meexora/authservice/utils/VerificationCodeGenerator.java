package com.meexora.authservice.utils;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;

@Component
public class VerificationCodeGenerator {

    private static final SecureRandom random = new SecureRandom();

    public String generateCode() {
        return new BigInteger(30, random).toString(32).toUpperCase();
    }
}