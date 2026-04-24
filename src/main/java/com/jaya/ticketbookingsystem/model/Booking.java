package com.jaya.ticketbookingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hold_id",nullable = false)
    private SeatHold hold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id",nullable = false)
    private Event event;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Integer seatsBooked;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // soft delete — cancel sets this
    private LocalDateTime deletedAt;


}
