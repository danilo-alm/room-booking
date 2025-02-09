package com.danilo.roombooking.service.amenity;

public class AmenityNotFoundException extends RuntimeException {
    public AmenityNotFoundException() {
        super("amenity not found");
    }
}
