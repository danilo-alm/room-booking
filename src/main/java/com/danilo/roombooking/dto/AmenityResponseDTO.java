package com.danilo.roombooking.dto;

import com.danilo.roombooking.domain.Amenity;

public record AmenityResponseDTO(Long id, String name) {
    public AmenityResponseDTO(Amenity amenity) {
        this(amenity.getId(), amenity.getName());
    }
}
