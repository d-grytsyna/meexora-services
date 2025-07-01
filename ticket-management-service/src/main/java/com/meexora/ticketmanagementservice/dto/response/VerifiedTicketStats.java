package com.meexora.ticketmanagementservice.dto.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerifiedTicketStats {
    private Integer validated;
}
