package com.danilo.roombooking.service.amenity;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class AmenityService {
    private final AmenityRepository amenityRepository;

    public Amenity createAmenity(AmenityRequestDTO amenityRequestDTO) {
        Amenity amenity = new Amenity();
        amenity.setName(amenityRequestDTO.name());
        return amenityRepository.save(amenity);
    }

    public Page<Amenity> getAmenities(Pageable pageable) {
        return amenityRepository.findAll(pageable);
    }

    public Page<Amenity> getAmenitiesWithPrefix(String prefix, Pageable pageable) {
        return amenityRepository.findByNameStartingWithIgnoreCase(prefix, pageable);
    }

    public void deleteAmenity(BigInteger id) {
        if (!amenityRepository.existsById(id)) {
            throw new AmenityNotFoundException();
        }
        amenityRepository.deleteById(id);
    }

}
