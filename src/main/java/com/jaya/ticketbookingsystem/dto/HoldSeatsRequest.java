package com.jaya.ticketbookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoldSeatsRequest {

    private UUID eventId;
    private UUID userId;
    private Integer seatsRequested;
}
