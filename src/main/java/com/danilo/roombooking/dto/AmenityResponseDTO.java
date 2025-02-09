package com.danilo.roombooking.dto;

import com.danilo.roombooking.domain.Amenity;

import java.math.BigInteger;

public record AmenityResponseDTO(BigInteger id, String name) {
    public AmenityResponseDTO(Amenity amenity) {
        this(amenity.getId(), amenity.getName());
    }
}
