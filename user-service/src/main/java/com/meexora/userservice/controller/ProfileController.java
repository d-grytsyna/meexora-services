package com.meexora.userservice.controller;


import com.meexora.common.response.ApiResponse;
import com.meexora.userservice.dto.UserProfileDto;
import com.meexora.userservice.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userService;


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(@RequestHeader("X-User-Id") String userId) {
        UserProfileDto userProfile = userService.getByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("User profile fetched", userProfile));
    }


    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> saveProfile(@RequestHeader("X-User-Id") String userId, @RequestBody @Valid UserProfileDto userProfileDto) {
        userService.createOrUpdateProfile(userId, userProfileDto);
        return ResponseEntity.ok(ApiResponse.success("User profile fetched", null));
    }
}
