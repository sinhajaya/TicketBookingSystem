package com.jaya.ticketbookingsystem.repository;

import com.jaya.ticketbookingsystem.model.Booking;
import com.jaya.ticketbookingsystem.model.HoldStatus;
import com.jaya.ticketbookingsystem.model.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold, UUID> {

    @Query("""
        SELECT COALESCE(SUM(h.seatsRequested), 0)
        FROM SeatHold h
        WHERE h.event.id = :eventId
          AND h.status = :status
          AND h.expiresAt > :now
    """)
    int sumActiveHeldSeats(UUID eventId, HoldStatus holdStatus, LocalDateTime now);

    @Query("""
        SELECT COUNT(h) 
        FROM SeatHold h
        WHERE h.userId = :userId
          AND h.event.id = :eventId
          AND h.status = :status
          AND h.expiresAt > :now
    """)
    boolean existsActiveHoldForUser(UUID userId, UUID eventId, HoldStatus status, LocalDateTime now);
}
