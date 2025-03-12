package com.danilo.roombooking.repository.jpa;

import com.danilo.roombooking.domain.Amenity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AmenityJpaRepository extends JpaRepository<Amenity, Long> {
    List<Amenity> findByIdIn(Collection<Long> ids);
    Page<Amenity> findByNameStartingWithIgnoreCase(String prefix, Pageable pageable);
}
