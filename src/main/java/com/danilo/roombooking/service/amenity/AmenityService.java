package com.danilo.roombooking.service.amenity;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;

    public Amenity create(AmenityRequestDTO amenityRequestDTO) {
        Amenity amenity = new Amenity();
        amenity.setName(amenityRequestDTO.name());
        return amenityRepository.save(amenity);
    }

    public Page<Amenity> getAll(Pageable pageable) {
        return amenityRepository.findAll(pageable);
    }

    public Page<Amenity> getWithPrefix(String prefix, Pageable pageable) {
        return amenityRepository.findByNameStartingWithIgnoreCase(prefix, pageable);
    }

    public void delete(Long id) {
        if (!amenityRepository.existsById(id)) {
            throw new AmenityNotFoundException();
        }
        amenityRepository.deleteById(id);
    }

}
