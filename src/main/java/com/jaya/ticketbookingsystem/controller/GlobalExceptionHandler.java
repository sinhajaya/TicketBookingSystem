package com.jaya.ticketbookingsystem.controller;

import com.jaya.ticketbookingsystem.exception.DuplicateBookingException;
import com.jaya.ticketbookingsystem.exception.HoldExpiredException;
import com.jaya.ticketbookingsystem.exception.ResourceNotFoundException;
import com.jaya.ticketbookingsystem.exception.SeatUnavailableException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(),null));
    }

    @ExceptionHandler(HoldExpiredException.class)
    public ResponseEntity<ErrorResponse> handleHoldExpired(HoldExpiredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(DuplicateBookingException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateBooking(DuplicateBookingException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(SeatUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleSeatUnavailable(SeatUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong", null));
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String message;
        private Object details;
        private LocalDateTime timestamp;

        public ErrorResponse(int status, String message, Object details) {
            this.status = status;
            this.message = message;
            this.details = details;
            this.timestamp = LocalDateTime.now();
        }
    }

}
