package com.meexora.eventservice.dto.response;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class EventShortResponse {

        private UUID id;

        private UUID creatorId;

        private String title;

        private String description;

        private OffsetDateTime date;

        private Double latitude;

        private Double longitude;

        private String address;
}
