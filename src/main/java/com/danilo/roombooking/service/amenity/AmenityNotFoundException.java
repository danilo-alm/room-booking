package com.danilo.roombooking.service.amenity;

public class AmenityNotFoundException extends RuntimeException {
    public AmenityNotFoundException(String message) {
        super(message);
    }
}
