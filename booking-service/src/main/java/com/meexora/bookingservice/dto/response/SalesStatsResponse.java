package com.meexora.bookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesStatsResponse {
    private List<TicketSalesPerDayDto> dailySales;
    private long totalTicketsSold;
    private BigDecimal totalRevenue;
}
