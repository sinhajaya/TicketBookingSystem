package com.jaya.ticketbookingsystem.repository;

import com.jaya.ticketbookingsystem.dto.BookingResponse;
import com.jaya.ticketbookingsystem.model.Booking;
import com.jaya.ticketbookingsystem.model.BookingStatus;
import com.jaya.ticketbookingsystem.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /**
     * Prevents double booking — same user already has a confirmed booking for this event.
     */
    @Query("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.userId = :userId
          AND b.event = :event
          AND b.status = :status
          AND b.deletedAt IS NULL
    """)
    boolean existsByUserIdAndEventAndStatus(UUID userId, Event event, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.deletedAt " +
            "IS NULL ORDER BY b.createdAt DESC")
    List<Booking> findActiveByUserId(UUID userId);

    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId AND b.deletedAt IS NULL")
    Optional<Booking> findActiveById(UUID bookingId);

    @Query("""
        SELECT COALESCE(SUM(b.seatsBooked), 0)
        FROM Booking b
        WHERE b.event.id = :eventId
          AND b.status = :status
          AND b.deletedAt IS NULL
    """)
    int sumSeatsByEventAndStatus(UUID eventId, BookingStatus bookingStatus);
}
