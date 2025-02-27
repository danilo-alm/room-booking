package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.repository.AmenityRepository;
import com.danilo.roombooking.service.amenity.AmenityNotFoundException;
import com.danilo.roombooking.service.amenity.AmenityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmenityServiceTest {

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private AmenityService amenityService;

    @Test
    public void AmenityService_CreateAmenity_ReturnsCreatedAmenity() {
        AmenityRequestDTO requestDTO = new AmenityRequestDTO("Projector");
        Amenity amenity = new Amenity();
        amenity.setName("Projector");

        when(amenityRepository.save(any(Amenity.class))).thenReturn(amenity);

        Amenity response = amenityService.create(requestDTO);

        assertNotNull(response);
        assertEquals("Projector", response.getName());

        verify(amenityRepository).save(any(Amenity.class));
    }

    @Test
    public void AmenityService_GetAmenities_ReturnsAllAmenities() {
        Amenity amenity = new Amenity();
        amenity.setName("Whiteboard");

        when(amenityRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(amenity)));

        Page<Amenity> response = amenityService.getAll(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.getTotalPages());
        assertEquals("Whiteboard", response.getContent().get(0).getName());

        verify(amenityRepository, never()).findAll();
        verify(amenityRepository).findAll(Pageable.unpaged());
    }

    @Test
    public void AmenityService_GetAmenitiesWithPrefix_ReturnsAmenitiesWithPrefix() {
        Amenity amenity = new Amenity();
        amenity.setName("Whiteboard");

        when(amenityRepository.findByNameStartingWithIgnoreCase("wh", Pageable.unpaged()))
            .thenReturn(new PageImpl<>(List.of(amenity)));

        Page<Amenity> response = amenityService.getWithPrefix("wh", Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Whiteboard", response.getContent().get(0).getName());

        verify(amenityRepository, never()).findAll();
        verify(amenityRepository).findByNameStartingWithIgnoreCase("wh", Pageable.unpaged());
    }

    @Test
    public void AmenityService_GetAmenitiesWithPrefix_ReturnsEmptyList() {
        when(amenityRepository.findByNameStartingWithIgnoreCase("wh", Pageable.unpaged()))
            .thenReturn(Page.empty());

        Page<Amenity> response = amenityService.getWithPrefix("wh", Pageable.unpaged());

        assertNotNull(response);
        assertEquals(0, response.getTotalElements());

        verify(amenityRepository).findByNameStartingWithIgnoreCase("wh", Pageable.unpaged());
    }

    @Test
    public void AmenityService_DeleteAmenity_DeletesAmenity() {
        BigInteger amenityId = BigInteger.ONE;
        when(amenityRepository.existsById(amenityId)).thenReturn(true);

        amenityService.delete(amenityId);

        verify(amenityRepository).deleteById(amenityId);
        verify(amenityRepository).existsById(amenityId);
    }

    @Test
    public void AmenityService_DeleteAmenity_ThrowsException_WhenAmenityNotFound() {
        when(amenityRepository.existsById(any())).thenReturn(false);

        assertThrows(AmenityNotFoundException.class, () -> amenityService.delete(BigInteger.ONE));

        verify(amenityRepository, never()).deleteById(any());
    }
}
