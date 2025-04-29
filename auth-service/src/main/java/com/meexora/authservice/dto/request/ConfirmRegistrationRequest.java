package com.meexora.authservice.dto.request;

import com.meexora.authservice.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConfirmRegistrationRequest {

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email must be less than 255 characters")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 6, max = 128, message = "Password must be between 6 and 128 characters")
    private String password;

    @NotNull
    private String role;

    @NotBlank(message = "Verification code must not be empty")
    private String code;
}
