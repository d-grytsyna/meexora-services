package com.meexora.eventservice.mapper;

import com.meexora.common.dto.EventDetailsDto;
import com.meexora.eventservice.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventDetailsMapper {


    @Mapping( source = "price", target = "ticketPrice")
    EventDetailsDto toDto(Event event);

    Event toEntity(EventDetailsDto dto);
}
