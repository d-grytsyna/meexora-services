package com.meexora.common.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventEditedMessage {
    private UUID eventId;
    private Integer addTickets;
    private Boolean locationChanged;
    private String location;
    private Boolean dateTimeChanged;
    private String dateTime;
}
