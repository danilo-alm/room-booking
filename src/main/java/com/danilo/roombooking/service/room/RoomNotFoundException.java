package com.danilo.roombooking.service.room;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException() {
        super("room not found");
    }
}
