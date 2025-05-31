package com.meexora.authservice.service;

import com.meexora.authservice.dto.request.AccountModificationRequest;
import com.meexora.authservice.dto.request.AuthRequest;
import com.meexora.authservice.dto.request.PasswordResetRequest;
import com.meexora.authservice.dto.response.AuthResponse;
import com.meexora.authservice.exception.EmailRequestRateLimitException;
import com.meexora.authservice.kafka.ForgotPasswordEventProducer;
import com.meexora.authservice.model.Role;
import com.meexora.authservice.model.User;
import com.meexora.authservice.repository.UserRepository;
import com.meexora.authservice.utils.VerificationCodeGenerator;
import com.meexora.common.exception.BadRequestException;
import com.meexora.common.exception.UnauthorizedException;
import com.meexora.common.exception.NotFoundException;
import com.meexora.common.kafka.AccountVerificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RegistrationTempService registrationTempService;
    private final VerificationCodeGenerator codeGenerator;
    private final ForgotPasswordEventProducer forgotPasswordEventProducer;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Account not found"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isValid(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String userId = jwtService.getSubject(refreshToken);
        Instant tokenIssuedAt = jwtService.getIssuedAt(refreshToken);

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new NotFoundException("User account not found"));

        Instant userStatus = user.getStatusUpdatedAt();

        if (tokenIssuedAt.truncatedTo(ChronoUnit.SECONDS)
                .isBefore(userStatus.truncatedTo(ChronoUnit.SECONDS))) {
            throw new UnauthorizedException("Token was issued before last account change");
        }



        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new NotFoundException("Account to log out not found"));

        user.setStatusUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    public void requestPasswordReset(AccountModificationRequest request) {
        userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("User not found"));
        if (registrationTempService.isRequestForgotPasswordBlocked(request.getEmail())) {
            throw new EmailRequestRateLimitException("Email request rate limit exceeded");
        }
        String verificationCode = codeGenerator.generateCode();
        registrationTempService.saveForgotPasswordCode(request.getEmail(), verificationCode);
        registrationTempService.blockForgotPasswordRequest(request.getEmail());
        forgotPasswordEventProducer.sendForgotPasswordRequest(new AccountVerificationMessage(request.getEmail(), verificationCode));
    }

    public AuthResponse confirmPasswordReset(PasswordResetRequest request) {
        String expectedCode = registrationTempService.getForgotPasswordCode(request.getEmail());
        if (expectedCode==null || !expectedCode.equals(request.getCode())) {
            throw new BadRequestException("Code expired or not found");
        }
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        registrationTempService.deleteForgotPasswordCode(request.getEmail());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }


}

