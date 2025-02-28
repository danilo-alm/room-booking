package com.danilo.roombooking.service.booking;

public class BookingConflictException extends RuntimeException {
    public BookingConflictException() {
        super("The room is occupied during the requested time slot.");
    }
}
