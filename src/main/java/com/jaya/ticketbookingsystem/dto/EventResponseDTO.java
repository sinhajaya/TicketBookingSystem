package com.jaya.ticketbookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDTO {

    private UUID id;
    private String name, location;
    private LocalDateTime eventDate;
    private int totalSeats;
    private int availableSeats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
