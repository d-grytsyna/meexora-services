package com.meexora.authservice.service;

import com.meexora.authservice.dto.request.AuthRequest;
import com.meexora.authservice.dto.response.AuthResponse;
import com.meexora.authservice.dto.request.RegisterRequest;
import com.meexora.authservice.model.Role;
import com.meexora.authservice.model.User;
import com.meexora.authservice.repository.RoleRepository;
import com.meexora.authservice.repository.UserRepository;
import com.meexora.common.exception.BadRequestException;
import com.meexora.common.exception.UnauthorizedException;
import com.meexora.common.exception.UserNotFoundException;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
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
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isValid(refreshToken)) {
            throw new UnauthorizedException(new String("Invalid refresh token"));
        }

        String userId = jwtService.getSubject(refreshToken);
        Instant tokenIssuedAt = jwtService.getIssuedAt(refreshToken);
        System.out.println(tokenIssuedAt);

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Instant userStatus = user.getStatusUpdatedAt();
        System.out.println(userStatus);

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
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setStatusUpdatedAt(Instant.now());
        userRepository.save(user);
    }


}

