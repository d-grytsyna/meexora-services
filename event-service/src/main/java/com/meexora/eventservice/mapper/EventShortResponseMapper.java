package com.meexora.eventservice.mapper;

import com.meexora.eventservice.dto.response.EventShortResponse;
import com.meexora.eventservice.model.Event;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventShortResponseMapper {
    List<EventShortResponse> toEventShortResponse(List<Event> events);
}
