package com.jaya.ticketbookingsystem.dto;

import com.jaya.ticketbookingsystem.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private UUID id;
    private UUID holdId;
    private UUID eventId;
    private UUID userId;
    private Integer seatsBooked;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
