package com.meexora.common.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountVerificationMessage {
    private String email;
    private String verificationCode;
}
