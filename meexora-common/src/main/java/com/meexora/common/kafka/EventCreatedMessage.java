package com.meexora.common.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreatedMessage {
    private UUID eventId;
    private Integer totalTickets;
    private LocalDateTime date;
}
