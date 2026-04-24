package com.jaya.ticketbookingsystem.service;

import com.jaya.ticketbookingsystem.dto.CreateEventRequest;
import com.jaya.ticketbookingsystem.dto.EventResponseDTO;
import com.jaya.ticketbookingsystem.exception.ResourceNotFoundException;
import com.jaya.ticketbookingsystem.model.Event;
import com.jaya.ticketbookingsystem.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SeatAvailabilityService seatAvailabilityService;

    @Transactional
    public EventResponseDTO createEvent(CreateEventRequest request){
        Event event = Event.builder()
                .name(request.getName())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .totalSeats(request.getTotalSeats())
                .build();
        Event saved = eventRepository.saveAndFlush(event);
        return toResponse(saved, event.getTotalSeats());
    }

    @Transactional(readOnly = true)
    public EventResponseDTO getEvent(UUID id) throws ResourceNotFoundException {
            Event event = findActiveOrThrow(id);
            int available = seatAvailabilityService.calculateAvailableSeats(id, event.getTotalSeats());
            return toResponse(event,available);
    }

    @Transactional
    public EventResponseDTO updateEvent(UUID id, CreateEventRequest request) throws ResourceNotFoundException {
        Event event = findActiveOrThrow(id);

        if(request.getName()!=null)
            event.setName(request.getName());
        if(request.getEventDate()!=null)
            event.setEventDate(request.getEventDate());
        if(request.getLocation()!=null)
            event.setLocation(request.getLocation());
        if(request.getTotalSeats()!=null)
            event.setTotalSeats(request.getTotalSeats());

        Event updated = eventRepository.save(event);
        int availableSeats = seatAvailabilityService.calculateAvailableSeats(id, event.getTotalSeats());
        return toResponse(updated, availableSeats);
    }

    @Transactional
    public void deleteEvent(UUID id) throws ResourceNotFoundException {
        Event event = findActiveOrThrow(id);
        event.setDeletedAt(LocalDateTime.now());
        eventRepository.save(event);
    }

    private Event findActiveOrThrow(UUID id) throws ResourceNotFoundException {
        return eventRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: "+id));
    }

    private EventResponseDTO toResponse(Event event, int availableSeats){
        return EventResponseDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .totalSeats(event.getTotalSeats())
                .availableSeats(availableSeats)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
