package com.danilo.roombooking.service.amenity;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityService {
    private final AmenityRepository amenityRepository;

    public Amenity createAmenity(AmenityRequestDTO amenityRequestDTO) {
        Amenity amenity = new Amenity();
        amenity.setName(amenityRequestDTO.name());
        return amenityRepository.save(amenity);
    }

    public List<Amenity> getAmenities(String prefix) {
        return prefix == null ? amenityRepository.findAll() :
            amenityRepository.findByNameStartingWithIgnoreCase(prefix);
    }

    public void deleteAmenity(BigInteger id) {
        if (!amenityRepository.existsById(id)) {
            throw new AmenityNotFoundException();
        }
        amenityRepository.deleteById(id);
    }

}
