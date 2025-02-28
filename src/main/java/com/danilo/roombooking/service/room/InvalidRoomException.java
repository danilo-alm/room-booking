package com.danilo.roombooking.service.room;

public class InvalidRoomException extends RuntimeException {
    public InvalidRoomException(String message) {
        super(message);
    }
}
