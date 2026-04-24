package com.jaya.ticketbookingsystem.controller;

import com.jaya.ticketbookingsystem.dto.BookingResponse;
import com.jaya.ticketbookingsystem.dto.ConfirmBookingRequest;
import com.jaya.ticketbookingsystem.exception.DuplicateBookingException;
import com.jaya.ticketbookingsystem.exception.HoldExpiredException;
import com.jaya.ticketbookingsystem.exception.ResourceNotFoundException;
import com.jaya.ticketbookingsystem.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@RequestBody ConfirmBookingRequest request) throws DuplicateBookingException, HoldExpiredException, ResourceNotFoundException {
        BookingResponse response = bookingService.confirmBooking(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/v1/bookings/{id}
     *
     * Fetch a single booking by its id.
     * Returns 404 if not found or already cancelled.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID id) throws ResourceNotFoundException {
        BookingResponse response = bookingService.getBooking(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/bookings?userId={userId}
     *
     * Fetch all active bookings for a user, latest first.
     * Returns empty list if no bookings found — not 404.
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getBookingsByUser(
            @RequestParam UUID userId) {
        List<BookingResponse> bookings = bookingService.getBookingsByUser(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * DELETE /api/v1/bookings/{id}?userId={userId}
     *
     * Cancel a booking — soft delete.
     * Only the booking owner can cancel.
     * Returns 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable UUID id,
            @RequestParam UUID userId) throws ResourceNotFoundException {
        bookingService.cancelBooking(id, userId);
        return ResponseEntity.noContent().build();
    }
}
