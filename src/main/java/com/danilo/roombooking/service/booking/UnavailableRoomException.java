package com.danilo.roombooking.service.booking;

public class UnavailableRoomException extends RuntimeException {
    public UnavailableRoomException(String message) {
        super(message);
    }
}
