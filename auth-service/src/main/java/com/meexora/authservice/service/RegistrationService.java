package com.meexora.authservice.service;

import com.meexora.authservice.dto.request.ConfirmRegistrationRequest;
import com.meexora.authservice.dto.request.AccountModificationRequest;
import com.meexora.authservice.dto.response.AuthResponse;
import com.meexora.authservice.kafka.RegistrationEventProducer;
import com.meexora.authservice.model.Role;
import com.meexora.authservice.model.User;
import com.meexora.authservice.repository.RoleRepository;
import com.meexora.authservice.repository.UserRepository;
import com.meexora.authservice.utils.VerificationCodeGenerator;
import com.meexora.common.exception.BadRequestException;
import com.meexora.authservice.exception.EmailRequestRateLimitException;
import com.meexora.common.kafka.AccountVerificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationTempService registrationTempService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeGenerator codeGenerator;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final RegistrationEventProducer registrationEventProducer;


    public void requestRegistration(AccountModificationRequest request) {
        if (registrationTempService.isRequestRegistrationBlocked(request.getEmail())) {
            throw new EmailRequestRateLimitException("Email request rate limit exceeded");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already in use");
        }
        String code = codeGenerator.generateCode();
        registrationTempService.saveVerificationCode(request.getEmail(), code);
        registrationTempService.blockRegistrationRequest(request.getEmail());
        AccountVerificationMessage message = new AccountVerificationMessage(request.getEmail(), code);
        registrationEventProducer.sendRegistrationRequest(message);
    }

    public AuthResponse confirmRegistration(ConfirmRegistrationRequest request) {
        String expectedCode = registrationTempService.getVerificationCode(request.getEmail());
        if (expectedCode== null || !expectedCode.equals(request.getCode())) {
            throw new BadRequestException("Verification code expired or not found");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already in use");
        }
        Role role = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> new BadRequestException("Role not found"));
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setCreatedAt(Instant.now());
        user.setStatusUpdatedAt(Instant.now());
        userRepository.save(user);
        registrationTempService.deleteVerificationCode(request.getEmail());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }

}