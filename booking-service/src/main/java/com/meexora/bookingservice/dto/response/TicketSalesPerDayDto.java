package com.meexora.bookingservice.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketSalesPerDayDto {

    private LocalDate date;
    private Long ticketsSold;
    private BigDecimal totalPrice;


}
