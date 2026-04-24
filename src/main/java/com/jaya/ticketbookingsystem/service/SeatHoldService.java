package com.jaya.ticketbookingsystem.service;

import com.jaya.ticketbookingsystem.dto.HoldResponse;
import com.jaya.ticketbookingsystem.dto.HoldSeatsRequest;
import com.jaya.ticketbookingsystem.exception.DuplicateBookingException;
import com.jaya.ticketbookingsystem.exception.ResourceNotFoundException;
import com.jaya.ticketbookingsystem.exception.SeatUnavailableException;
import com.jaya.ticketbookingsystem.model.Event;
import com.jaya.ticketbookingsystem.model.HoldStatus;
import com.jaya.ticketbookingsystem.model.SeatHold;
import com.jaya.ticketbookingsystem.repository.EventRepository;
import com.jaya.ticketbookingsystem.repository.SeatHoldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private static final int HOLD_DURATION_MINUTES = 5;

    private final SeatHoldRepository seatHoldRepository;
    private final EventRepository eventRepository;
    private final SeatAvailabilityService seatAvailabilityService;

    @Transactional
    public HoldResponse holdSeats(HoldSeatsRequest request) throws ResourceNotFoundException, DuplicateBookingException, SeatUnavailableException {

        // Acquire lock on Event row — blocks concurrent hold requests for same event
        Event event = eventRepository.findActiveByIdWithLock(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found: " + request.getEventId()));

        // Prevent same user from holding seats multiple times for the same event
        boolean alreadyHolding = seatHoldRepository.existsActiveHoldForUser(
                request.getUserId(), event.getId(), HoldStatus.ACTIVE, LocalDateTime.now());
        if (alreadyHolding) {
            throw new DuplicateBookingException(
                    "You already have an active hold for this event. Please confirm or wait for it to expire.");
        }

        // Calculate real-time available seats under lock
        int availableSeats = seatAvailabilityService
                .calculateAvailableSeats(event.getId(), event.getTotalSeats());

        if (availableSeats < request.getSeatsRequested()) {
            throw new SeatUnavailableException(
                    "Not enough seats available. Requested: " + request.getSeatsRequested()
                            + ", Available: " + availableSeats);
        }

        // Create hold — expires in 5 minutes
        SeatHold hold = SeatHold.builder()
                .event(event)
                .userId(request.getUserId())
                .seatsRequested(request.getSeatsRequested())
                .status(HoldStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES))
                .build();

        SeatHold saved = seatHoldRepository.save(hold);
        return toResponse(saved);

    }

    @Transactional(readOnly = true)
    public HoldResponse getHold(UUID holdId) throws ResourceNotFoundException {
        SeatHold hold = seatHoldRepository.findById(holdId)
                .orElseThrow(() -> new ResourceNotFoundException("Hold not found: " + holdId));
        return toResponse(hold);
    }

    private HoldResponse toResponse(SeatHold hold) {
        return HoldResponse.builder()
                .holdId(hold.getId())
                .eventId(hold.getEvent().getId())
                .userId(hold.getUserId())
                .seatsRequested(hold.getSeatsRequested())
                .status(hold.getStatus())
                .expiresAt(hold.getExpiresAt())
                .createdAt(hold.getCreatedAt())
                .build();
    }
}
