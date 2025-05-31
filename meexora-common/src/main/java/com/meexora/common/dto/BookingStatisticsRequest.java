package com.meexora.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatisticsRequest {

    Map<UUID, OffsetDateTime> lastPriceChangeTimestamps;

}
