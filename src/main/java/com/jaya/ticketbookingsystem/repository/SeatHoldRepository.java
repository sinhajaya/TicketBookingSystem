package com.jaya.ticketbookingsystem.repository;

import com.jaya.ticketbookingsystem.model.HoldStatus;
import com.jaya.ticketbookingsystem.model.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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
    Integer sumActiveHeldSeats(UUID eventId, HoldStatus status, LocalDateTime now);

    @Query("""
        SELECT COUNT(h)
        FROM SeatHold h
        WHERE h.userId = :userId
          AND h.event.id = :eventId
          AND h.status = :status
          AND h.expiresAt > :now
    """)
    Long countActiveHoldsForUser(UUID userId, UUID eventId, HoldStatus status, LocalDateTime now);

    @Query("""
        SELECT h FROM SeatHold h
        WHERE h.status = :status
          AND h.expiresAt < :now
    """)
    List<SeatHold> findExpiredHolds(HoldStatus status, LocalDateTime now);
}
