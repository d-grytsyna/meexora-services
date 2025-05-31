package com.meexora.common.kafka;

import com.meexora.common.dto.EmailTicketDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketEmailMessage {
    private UUID bookingId;
    private String userEmail;
    private List<EmailTicketDto> tickets;
}
