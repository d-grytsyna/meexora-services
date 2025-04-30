package com.meexora.authservice.utils;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;

@Component
public class VerificationCodeGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final int CODE_LENGTH = 6;


    public String generateCode() {
        int max = (int) Math.pow(10, CODE_LENGTH) - 1;
        int code = random.nextInt(max + 1);

        return String.format("%0" + CODE_LENGTH + "d", code);
    }
}