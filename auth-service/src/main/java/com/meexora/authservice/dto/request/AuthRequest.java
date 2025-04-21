package com.meexora.authservice.dto.request;


import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
