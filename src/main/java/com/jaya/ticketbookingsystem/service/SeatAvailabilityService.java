package com.jaya.ticketbookingsystem.service;

import com.jaya.ticketbookingsystem.model.BookingStatus;
import com.jaya.ticketbookingsystem.model.HoldStatus;
import com.jaya.ticketbookingsystem.repository.BookingRepository;
import com.jaya.ticketbookingsystem.repository.SeatHoldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatAvailabilityService {

    private final BookingRepository bookingRepository;
    private final SeatHoldRepository seatHoldRepository;

    /**
     * Available = totalSeats - confirmed bookings - active (non-expired) holds
     * Called both on Event read and before creating a new hold.
     */
    public Integer calculateAvailableSeats(UUID eventId, Integer totalSeats) {
        Integer confirmedSeats = bookingRepository
                .sumSeatsByEventAndStatus(eventId, BookingStatus.CONFIRMED);

        Integer heldSeats = seatHoldRepository
                .sumActiveHeldSeats(eventId, HoldStatus.ACTIVE, LocalDateTime.now());
        return totalSeats - confirmedSeats - heldSeats;
    }
}
