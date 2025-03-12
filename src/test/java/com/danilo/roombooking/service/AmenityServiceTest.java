package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.repository.jpa.AmenityJpaRepository;
import com.danilo.roombooking.service.amenity.AmenityNotFoundException;
import com.danilo.roombooking.service.amenity.AmenityService;
import com.danilo.roombooking.service.amenity.InvalidAmenityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmenityServiceTest {

    @Mock
    private AmenityJpaRepository amenityJpaRepository;

    @InjectMocks
    private AmenityService amenityService;

    AmenityRequestDTO amenityDTO;
    Amenity amenity;

    @BeforeEach
    void setUp() {
        amenityDTO = new AmenityRequestDTO("Whiteboard");
        amenity = new Amenity(1L, "Whiteboard", null);
    }

    @Test
    public void AmenityService_Create_ReturnsCreatedAmenity() {
        when(amenityJpaRepository.save(any(Amenity.class))).thenReturn(amenity);

        Amenity response = amenityService.create(amenityDTO);

        assertNotNull(response);
        assertEquals(amenityDTO.name(), response.getName());

        verify(amenityJpaRepository).save(any(Amenity.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    public void AmenityService_Create_ThrowsException_WhenNameIsInvalid(String name) {
        AmenityRequestDTO invalidAmenityRequestDTO = new AmenityRequestDTO(name);

        InvalidAmenityException exception = assertThrows(
            InvalidAmenityException.class, () -> amenityService.create(invalidAmenityRequestDTO));

        assertEquals("name is required.", exception.getMessage());
    }

    @Test
    public void AmenityService_GetAll_ReturnsAllAmenities() {
        when(amenityJpaRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(amenity)));

        Page<Amenity> response = amenityService.getAll(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.getTotalPages());
        assertEquals(amenity.getName(), response.getContent().get(0).getName());

        verify(amenityJpaRepository).findAll(Pageable.unpaged());
    }

    @Test
    public void AmenityService_GetWithPrefix_ReturnsAmenitiesWithPrefix() {
        when(amenityJpaRepository.findByNameStartingWithIgnoreCase("wh", Pageable.unpaged()))
            .thenReturn(new PageImpl<>(List.of(amenity)));

        Page<Amenity> response = amenityService.getWithPrefix("wh", Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(amenity.getName(), response.getContent().get(0).getName());

        verify(amenityJpaRepository).findByNameStartingWithIgnoreCase("wh", Pageable.unpaged());
    }

    @Test
    public void AmenityService_Delete_DeletesAmenity() {
        when(amenityJpaRepository.existsById(any())).thenReturn(true);

        amenityService.delete(amenity.getId());

        verify(amenityJpaRepository).existsById(amenity.getId());
        verify(amenityJpaRepository).deleteById(amenity.getId());
    }

    @Test
    public void AmenityService_Delete_ThrowsException_WhenAmenityNotFound() {
        when(amenityJpaRepository.existsById(any())).thenReturn(false);

        assertThrows(AmenityNotFoundException.class, () -> amenityService.delete(1L));

        verify(amenityJpaRepository).existsById(any());
        verify(amenityJpaRepository, never()).deleteById(any());
    }
}
