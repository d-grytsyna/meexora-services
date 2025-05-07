package com.meexora.bookingservice.mapper;

import com.meexora.bookingservice.dto.response.BookingResponseDto;
import com.meexora.bookingservice.dto.response.TicketDto;
import com.meexora.bookingservice.model.Booking;
import com.meexora.bookingservice.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "status", expression = "java(booking.getStatus().name())")
    @Mapping(target = "tickets", expression = "java(toTicketDtoList(booking.getTickets()))")
    BookingResponseDto toDto(Booking booking);

    @Mapping(target = "status", expression = "java(ticket.getStatus().name())")
    TicketDto toTicketDto(Ticket ticket);

    List<TicketDto> toTicketDtoList(List<Ticket> tickets);
}
