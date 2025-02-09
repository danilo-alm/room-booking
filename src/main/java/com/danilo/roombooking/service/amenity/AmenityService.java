package com.danilo.roombooking.service.amenity;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.dto.AmenityResponseDTO;
import com.danilo.roombooking.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmenityService {
    private final AmenityRepository amenityRepository;

    public AmenityResponseDTO createAmenity(AmenityRequestDTO amenityRequestDTO) {
        Amenity amenity = new Amenity();
        amenity.setName(amenityRequestDTO.name());
        return new AmenityResponseDTO(amenityRepository.save(amenity));
    }

    public List<AmenityResponseDTO> getAmenities(String prefix) {
        if (prefix == null) {
            return amenityRepository.findAll().stream()
                .map(AmenityResponseDTO::new).collect(Collectors.toList());
        }
        return amenityRepository.findByNameStartingWithIgnoreCase(prefix).stream()
            .map(AmenityResponseDTO::new).collect(Collectors.toList());
    }

    public List<Amenity> getAmenities(Collection<BigInteger> ids) {
        return amenityRepository.findByIdIn(ids);
    }

    public void deleteAmenity(BigInteger id) {
        if (!amenityRepository.existsById(id)) {
            throw new AmenityNotFoundException();
        }
        amenityRepository.deleteById(id);
    }

}
