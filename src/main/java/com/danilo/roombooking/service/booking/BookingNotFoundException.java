package com.danilo.roombooking.service.booking;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException() {
        super("Booking not found.");
    }
}
