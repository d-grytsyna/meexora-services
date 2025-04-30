package com.meexora.userservice.mapper;

import com.meexora.userservice.dto.UserProfileDto;
import com.meexora.userservice.model.UserProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfile toEntity(UserProfileDto dto);

    UserProfileDto toDto(UserProfile entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserProfileDto dto, @MappingTarget UserProfile entity);
}