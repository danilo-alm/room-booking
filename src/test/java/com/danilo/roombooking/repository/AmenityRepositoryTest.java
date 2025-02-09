package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.Amenity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class AmenityRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private AmenityRepository amenityRepository;

    @Test
    public void AmenityRepository_FindById_ReturnsAmenity() {
        Amenity amenity = createAndSaveAmenity("Projector");

        Optional<Amenity> amenityOptional = amenityRepository.findById(amenity.getId());

        assertTrue(amenityOptional.isPresent());
        assertEquals(amenity.getId(), amenityOptional.get().getId());
    }

    @Test
    public void AmenityRepository_FindByIdIn_ReturnsAmenities() {
        Amenity amenity1 = createAndSaveAmenity("Projector");
        Amenity amenity2 = createAndSaveAmenity("Whiteboard");

        List<Amenity> amenities = amenityRepository.findByIdIn(Set.of(amenity1.getId(), amenity2.getId()));

        assertEquals(2, amenities.size());
        assertTrue(amenities.contains(amenity1));
        assertTrue(amenities.contains(amenity2));
    }

    @Test
    public void AmenityRepository_FindByIdIn_ReturnsEmptyList_WhenNoMatch() {
        List<Amenity> amenities = amenityRepository.findByIdIn(Set.of(BigInteger.valueOf(999)));

        assertTrue(amenities.isEmpty());
    }

    @Test
    public void AmenityRepository_FindByNameStartingWithIgnoreCase_ReturnsMatchingAmenities() {
        Amenity amenity1 = createAndSaveAmenity("Projector");
        Amenity amenity2 = createAndSaveAmenity("Projection Screen");
        createAndSaveAmenity("Whiteboard");

        List<Amenity> amenities = amenityRepository.findByNameStartingWithIgnoreCase("proj");

        assertEquals(2, amenities.size());
        assertTrue(amenities.contains(amenity1));
        assertTrue(amenities.contains(amenity2));
    }

    @Test
    public void AmenityRepository_FindByNameStartingWithIgnoreCase_ReturnsEmptyList_WhenNoMatch() {
        createAndSaveAmenity("Whiteboard");

        List<Amenity> amenities = amenityRepository.findByNameStartingWithIgnoreCase("comp");

        assertTrue(amenities.isEmpty());
    }

    @Test
    public void AmenityRepository_Save_UpdatesAmenity() {
        Amenity amenity = createAndSaveAmenity("Projector");
        amenity.setName("Updated Projector");
        amenityRepository.save(amenity);

        Optional<Amenity> updatedAmenity = amenityRepository.findById(amenity.getId());

        assertTrue(updatedAmenity.isPresent());
        assertEquals("Updated Projector", updatedAmenity.get().getName());
    }

    @Test
    public void AmenityRepository_DeleteById_DeletesAmenity() {
        Amenity amenity = createAndSaveAmenity("Projector");
        amenityRepository.deleteById(amenity.getId());

        Optional<Amenity> deletedAmenity = amenityRepository.findById(amenity.getId());

        assertFalse(deletedAmenity.isPresent());
    }

    private Amenity createAndSaveAmenity(String name) {
        Amenity amenity = new Amenity();
        amenity.setName(name);
        return amenityRepository.save(amenity);
    }
}
