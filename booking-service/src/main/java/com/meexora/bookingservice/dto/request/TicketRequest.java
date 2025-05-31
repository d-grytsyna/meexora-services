package com.meexora.bookingservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class TicketRequest {


    @NotEmpty
    private String userName;
}