package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.ApiPaths;
import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.dto.AmenityResponseDTO;
import com.danilo.roombooking.service.amenity.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.Amenity.ROOT)
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @PostMapping(ApiPaths.Amenity.CREATE)
    public ResponseEntity<AmenityResponseDTO> create(@RequestBody AmenityRequestDTO amenityRequestDTO) {
        Amenity amenity = amenityService.create(amenityRequestDTO);
        return ResponseEntity.ok(new AmenityResponseDTO(amenity));
    }

    @GetMapping(ApiPaths.Amenity.GET)
    public ResponseEntity<Page<AmenityResponseDTO>> getAllOrWithPrefix(
        @RequestParam(required = false) String prefix,
        @PageableDefault Pageable pageable
    ) {
        Page<Amenity> amenities;
        if (prefix != null)
            amenities = amenityService.getWithPrefix(prefix, pageable);
        else
            amenities = amenityService.getAll(pageable);

        return ResponseEntity.ok(amenities.map(AmenityResponseDTO::new));
    }

    @DeleteMapping(ApiPaths.Amenity.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        amenityService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
