package com.jaya.ticketbookingsystem.service;

import com.jaya.ticketbookingsystem.dto.BookingResponse;
import com.jaya.ticketbookingsystem.dto.ConfirmBookingRequest;
import com.jaya.ticketbookingsystem.exception.DuplicateBookingException;
import com.jaya.ticketbookingsystem.exception.HoldExpiredException;
import com.jaya.ticketbookingsystem.exception.ResourceNotFoundException;
import com.jaya.ticketbookingsystem.model.*;
import com.jaya.ticketbookingsystem.repository.BookingRepository;
import com.jaya.ticketbookingsystem.repository.EventRepository;
import com.jaya.ticketbookingsystem.repository.SeatHoldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final SeatHoldRepository seatHoldRepository;

    @Transactional
    public BookingResponse confirmBooking(@RequestBody ConfirmBookingRequest request) throws ResourceNotFoundException, HoldExpiredException, DuplicateBookingException {
        SeatHold hold = seatHoldRepository.findById(request.getHoldId())
                .orElseThrow(()-> new ResourceNotFoundException( "Hold not found: " + request.getHoldId()));

        // Hold must be active
        if(hold.getStatus() != HoldStatus.ACTIVE){
            throw new HoldExpiredException("Hold is no longer active. Status: " + hold.getStatus());
        }

        // Hold must not have passed its expiry window
        if (hold.getExpiresAt().isBefore(LocalDateTime.now())) {
            hold.setStatus(HoldStatus.EXPIRED);
            seatHoldRepository.save(hold);
            throw new HoldExpiredException("Hold has expired. Please request a new hold.");
        }

        // User must own the hold
        if (!hold.getUserId().equals(request.getUserId())) {
            throw new ResourceNotFoundException("Hold does not belong to this user.");
        }

        // Fetch Event entity — needed for @ManyToOne relationship in Booking
        Event event = eventRepository.findActiveById(hold.getEvent().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found for this hold."));

        // Prevent duplicate confirmed booking for same user + event
        boolean alreadyBooked = bookingRepository.existsByUserIdAndEventAndStatus(
                request.getUserId(), event, BookingStatus.CONFIRMED);
        if (alreadyBooked) {
            throw new DuplicateBookingException(
                    "User already has a confirmed booking for this event.");
        }

        // Mark hold as confirmed — seats are now permanently taken
        hold.setStatus(HoldStatus.CONFIRMED);
        seatHoldRepository.save(hold);

        // Create booking with proper entity references
        Booking booking = Booking.builder()
                .event(event)
                .hold(hold)
                .userId(hold.getUserId())
                .seatsBooked(hold.getSeatsRequested())
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(UUID bookingId) throws ResourceNotFoundException {
        Booking booking = bookingRepository.findActiveById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        return toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(UUID userId) {
        return bookingRepository.findActiveByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void cancelBooking(UUID bookingId, UUID userId) throws ResourceNotFoundException {
        Booking booking = bookingRepository.findActiveById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Booking does not belong to this user.");
        }

        booking.setDeletedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Free up seats — mark hold as EXPIRED so availability recalculates correctly
        SeatHold hold = booking.getHold();
        if (hold != null) {
            hold.setStatus(HoldStatus.EXPIRED);
            seatHoldRepository.save(hold);
        }
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .holdId(booking.getHold().getId())
                .eventId(booking.getEvent().getId())
                .userId(booking.getUserId())
                .seatsBooked(booking.getSeatsBooked())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
