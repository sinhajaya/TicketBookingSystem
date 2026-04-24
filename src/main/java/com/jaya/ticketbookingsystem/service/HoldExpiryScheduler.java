package com.jaya.ticketbookingsystem.service;

import com.jaya.ticketbookingsystem.model.HoldStatus;
import com.jaya.ticketbookingsystem.model.SeatHold;
import com.jaya.ticketbookingsystem.repository.SeatHoldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HoldExpiryScheduler {

    private final SeatHoldRepository seatHoldRepository;

    /**
     * Runs every 60 seconds.
     * Finds all ACTIVE holds past their expiresAt and marks them EXPIRED.
     * This releases their seats back into availability automatically.
     *
     * Why 60 seconds and not 5 minutes?
     * Shorter interval = seats freed faster = better UX for waiting users.
     * Overhead is minimal — query targets a small indexed subset.
     */
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void expireStaleHolds() {
        List<SeatHold> expiredHolds = seatHoldRepository
                .findExpiredHolds(HoldStatus.ACTIVE, LocalDateTime.now());

        if (expiredHolds.isEmpty()) {
            return;
        }

        expiredHolds.forEach(hold -> hold.setStatus(HoldStatus.EXPIRED));
        seatHoldRepository.saveAll(expiredHolds);

        log.info("Expired {} stale seat holds", expiredHolds.size());
    }
}
