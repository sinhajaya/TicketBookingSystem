package com.jaya.ticketbookingsystem.dto;

import com.jaya.ticketbookingsystem.model.HoldStatus;
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
public class HoldResponse {

    private UUID holdId;
    private UUID eventId;
    private UUID userId;
    private Integer seatsRequested;
    private HoldStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
