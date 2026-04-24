package com.jaya.ticketbookingsystem.controller;

import com.jaya.ticketbookingsystem.dto.HoldResponse;
import com.jaya.ticketbookingsystem.dto.HoldSeatsRequest;
import com.jaya.ticketbookingsystem.exception.DuplicateBookingException;
import com.jaya.ticketbookingsystem.exception.ResourceNotFoundException;
import com.jaya.ticketbookingsystem.exception.SeatUnavailableException;
import com.jaya.ticketbookingsystem.service.SeatHoldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/holds")
@RequiredArgsConstructor
public class SeatHoldController {

    private final SeatHoldService seatHoldService;

    /**
     * POST /api/v1/holds
     *
     * Request a temporary seat hold for an event.
     * Hold is valid for 5 minutes — client must confirm within this window.
     * Returns 201 Created with holdId and expiresAt timestamp.
     *
     * This is where the pessimistic lock on the Event row fires —
     * concurrent requests for the same event are serialized here.
     *
     * Request body:
     * {
     *   "eventId": "uuid",
     *   "userId": "uuid",
     *   "seatsRequested": 2
     * }
     */
    @PostMapping
    public ResponseEntity<HoldResponse> holdSeats(
            @RequestBody HoldSeatsRequest request) throws ResourceNotFoundException, SeatUnavailableException, DuplicateBookingException {
        HoldResponse response = seatHoldService.holdSeats(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/holds/{holdId}
     *
     * Check the current status of a hold.
     * Client can use this to verify hold is still ACTIVE before confirming.
     * Also returns expiresAt so client can show a countdown.
     */
    @GetMapping("/{holdId}")
    public ResponseEntity<HoldResponse> getHold(@PathVariable UUID holdId) throws ResourceNotFoundException {
        HoldResponse response = seatHoldService.getHold(holdId);
        return ResponseEntity.ok(response);
    }
}
