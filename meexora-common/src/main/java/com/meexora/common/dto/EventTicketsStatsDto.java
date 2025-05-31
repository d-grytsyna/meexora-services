package com.meexora.common.dto;

import java.util.UUID;

public record EventTicketsStatsDto(
        UUID eventId,
        int soldTickets,
        int totalTicketsLeft
) {}