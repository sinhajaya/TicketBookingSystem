package com.jaya.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AvailabilityResponse {

    private UUID eventId;
    private String eventName;
    private Integer totalSeats;
    private Integer confirmedSeats;   // seats locked by confirmed bookings
    private Integer heldSeats;        // seats temporarily held (active holds)
    private Integer availableSeats;   // totalSeats - confirmedSeats - heldSeats
}