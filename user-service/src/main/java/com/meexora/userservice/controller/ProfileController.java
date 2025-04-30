package com.meexora.userservice.controller;


import com.meexora.common.response.ApiResponse;
import com.meexora.userservice.dto.UserProfileDto;
import com.meexora.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @PostMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> logout(@RequestHeader("X-User-Id") String userId) {
//        userService.getData(userId);
        return ResponseEntity.ok(ApiResponse.success("User profile fetched", new UserProfileDto()));
    }
}
