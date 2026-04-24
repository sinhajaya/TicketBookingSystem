package com.jaya.ticketbookingsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEventRequest {

    private String name, location;
    private Integer totalSeats;
    private LocalDateTime eventDate;
}
