package com.meexora.eventservice.mapper;


import com.meexora.eventservice.dto.request.CreateEventRequest;
import com.meexora.eventservice.dto.response.EventResponse;
import com.meexora.eventservice.model.Event;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event toEntity(CreateEventRequest request);

    default Event toEntity(CreateEventRequest request, UUID creatorId) {
        Event event = toEntity(request);
        event.setCreatorId(creatorId);
        return event;
    }
    EventResponse toResponse(Event entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(CreateEventRequest request, @MappingTarget Event entity);
}
