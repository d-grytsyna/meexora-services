package com.meexora.userservice.service;


import com.meexora.common.exception.NotFoundException;
import com.meexora.userservice.dto.UserProfileDto;
import com.meexora.userservice.mapper.UserProfileMapper;
import com.meexora.userservice.model.UserProfile;
import com.meexora.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;


    public UserProfileDto getByUserId(String userId) {
        UserProfile profile = userProfileRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new NotFoundException("User profile not found"));
        return userProfileMapper.toDto(profile);
    }

    public void createOrUpdateProfile(String userId, UserProfileDto dto) {
        UserProfile profile = userProfileRepository.findByUserId(UUID.fromString(userId))
                .orElseGet(() -> UserProfile.builder().userId(UUID.fromString(userId)).build());

        userProfileMapper.updateEntityFromDto(dto, profile);
        userProfileRepository.save(profile);
    }
}
