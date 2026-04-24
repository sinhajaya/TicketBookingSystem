package com.jaya.ticketbookingsystem.controller;

import com.jaya.ticketbookingsystem.dto.CreateEventRequest;
import com.jaya.ticketbookingsystem.dto.EventResponseDTO;
import com.jaya.ticketbookingsystem.exception.ResourceNotFoundException;
import com.jaya.ticketbookingsystem.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody CreateEventRequest eventRequestDTO){
        EventResponseDTO eventResponseDTO = eventService.createEvent(eventRequestDTO);
        return new ResponseEntity<>(eventResponseDTO,HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable UUID id) throws ResourceNotFoundException {
        EventResponseDTO response = eventService.getEvent(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable UUID id,
                                                        @RequestBody CreateEventRequest request) throws ResourceNotFoundException {
        EventResponseDTO response= eventService.updateEvent(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeEvent(@PathVariable UUID id) throws ResourceNotFoundException {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}
