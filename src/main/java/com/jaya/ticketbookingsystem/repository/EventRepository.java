package com.jaya.ticketbookingsystem.repository;

import com.jaya.ticketbookingsystem.model.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    // Excludes soft-deleted records for normal reads
    @Query("SELECT e FROM Event e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<Event> findActiveById(UUID id);

    // Pessimistic write lock — used during hold creation to prevent overbooking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<Event> findActiveByIdWithLock(UUID id);
}
