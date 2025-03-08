package com.danilo.roombooking.service.booking;

public class BookingConflictException extends RuntimeException {
    public BookingConflictException() {
        super("Room is unavailable or occupied during the requested time slot.");
    }
}
