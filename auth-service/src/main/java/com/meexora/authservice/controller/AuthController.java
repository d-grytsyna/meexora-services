package com.meexora.authservice.controller;


import com.meexora.authservice.dto.request.*;
import com.meexora.authservice.dto.response.AuthResponse;
import com.meexora.authservice.service.AuthService;
import com.meexora.authservice.service.RegistrationService;
import com.meexora.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegistrationService registrationService;


    @PostMapping("/registration/request")
    public ResponseEntity<ApiResponse<Void>> registerRequest(@Valid @RequestBody AccountModificationRequest request) {
        registrationService.requestRegistration(request);
        return ResponseEntity.ok(ApiResponse.success("Registration request successful" , null));
    }

    @PostMapping("/registration/confirm")
    public ResponseEntity<ApiResponse<AuthResponse>> registerConfirm(@Valid @RequestBody ConfirmRegistrationRequest request) {
        AuthResponse response = registrationService.confirmRegistration(request);
        return ResponseEntity.ok(ApiResponse.success("Registration confirmed", response));
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<ApiResponse<Void>> passwordResetRequest(@Valid @RequestBody AccountModificationRequest request) {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset request successful", null));
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<ApiResponse<AuthResponse>> passwordResetConfirm(@Valid @RequestBody PasswordResetRequest request) {
        AuthResponse response = authService.confirmPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success("Password has been successfully reset", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody TokenRefreshRequest request) {
        AuthResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("X-User-Id") String userId) {
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success("Logged out", null));
    }


}
