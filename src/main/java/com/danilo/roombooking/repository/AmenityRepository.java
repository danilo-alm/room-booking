package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.Amenity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    List<Amenity> findByIdIn(Collection<Long> ids);
    Page<Amenity> findByNameStartingWithIgnoreCase(String prefix, Pageable pageable);
}
