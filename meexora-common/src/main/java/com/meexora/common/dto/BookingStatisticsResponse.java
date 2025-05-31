package com.meexora.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;



@Data
@NoArgsConstructor
@AllArgsConstructor

public class BookingStatisticsResponse {
    List<EventTicketsStatsDto> eventTicketsStats;

}

