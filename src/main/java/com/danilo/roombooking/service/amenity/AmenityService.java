package com.danilo.roombooking.service.amenity;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;

    public Amenity create(AmenityRequestDTO amenityRequestDTO) {
        validateAmenityRequest(amenityRequestDTO);

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

    public List<Amenity> getByIdIn(Collection<Long> ids) {
        List<Amenity> amenities = amenityRepository.findByIdIn(ids);
        if (amenities.size() == ids.size())
            return amenities;

        Set<Long> foundIds = amenities.stream().map(Amenity::getId).collect(Collectors.toSet());
        Set<Long> missingIds = ids.stream()
            .filter(id -> !foundIds.contains(id))
            .collect(Collectors.toSet());
        throw new AmenityNotFoundException("amenities not found: " + missingIds);
    }

    public void delete(Long id) {
        if (!amenityRepository.existsById(id)) {
            throw new AmenityNotFoundException("amenity not found.");
        }
        amenityRepository.deleteById(id);
    }

    private void validateAmenityRequest(AmenityRequestDTO amenity) {
        if (amenity.name() == null || amenity.name().isBlank())
            throw new InvalidAmenityException("name is required.");
    }

}
