package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.dto.AmenityResponseDTO;
import com.danilo.roombooking.repository.AmenityRepository;
import com.danilo.roombooking.service.amenity.AmenityNotFoundException;
import com.danilo.roombooking.service.amenity.AmenityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        AmenityResponseDTO response = amenityService.createAmenity(requestDTO);

        assertNotNull(response);
        assertEquals("Projector", response.name());

        verify(amenityRepository).save(any(Amenity.class));
    }

    @Test
    public void AmenityService_GetAmenities_NoPrefix_ReturnsAllAmenities() {
        Amenity amenity = new Amenity();
        amenity.setName("Whiteboard");

        when(amenityRepository.findByNameStartingWithIgnoreCase("wh"))
            .thenReturn(List.of(amenity));

        List<AmenityResponseDTO> response = amenityService.getAmenities("wh");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Whiteboard", response.get(0).name());

        verify(amenityRepository, never()).findAll();
        verify(amenityRepository).findByNameStartingWithIgnoreCase("wh");
    }

    @Test
    public void AmenityService_GetAmenities_WithPrefix_ReturnsAmenitiesWithPrefix() {
        Amenity amenity = new Amenity();
        amenity.setName("Whiteboard");

        when(amenityRepository.findByNameStartingWithIgnoreCase("wh"))
            .thenReturn(List.of(amenity));

        List<AmenityResponseDTO> response = amenityService.getAmenities("wh");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Whiteboard", response.get(0).name());

        verify(amenityRepository, never()).findAll();
        verify(amenityRepository).findByNameStartingWithIgnoreCase("wh");
    }

    @Test
    public void AmenityService_GetAmenities_WithPrefix_ReturnsEmptyList() {
        when(amenityRepository.findByNameStartingWithIgnoreCase("wh")).thenReturn(List.of());

        List<AmenityResponseDTO> result = amenityService.getAmenities("wh");

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(amenityRepository).findByNameStartingWithIgnoreCase("wh");
    }

    @Test
    public void AmenityService_DeleteAmenity_DeletesAmenity() {
        BigInteger amenityId = BigInteger.ONE;
        when(amenityRepository.existsById(amenityId)).thenReturn(true);

        amenityService.deleteAmenity(amenityId);

        verify(amenityRepository).deleteById(amenityId);
        verify(amenityRepository).existsById(amenityId);
    }

    @Test
    public void AmenityService_DeleteAmenity_ThrowsException_WhenAmenityNotFound() {
        when(amenityRepository.existsById(any())).thenReturn(false);

        assertThrows(AmenityNotFoundException.class, () -> amenityService.deleteAmenity(BigInteger.ONE));

        verify(amenityRepository, never()).deleteById(any());
    }
}
