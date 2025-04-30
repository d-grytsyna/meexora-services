package com.meexora.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileDto {
    @NotBlank(message = "First name is required")
    @Size(max = 64, message = "First name must be at most 64 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 64, message = "Last name must be at most 64 characters")
    private String lastName;

    private LocalDate birthdate;

    @Size(max = 128, message = "Location must be at most 128 characters")
    private String location;
}
