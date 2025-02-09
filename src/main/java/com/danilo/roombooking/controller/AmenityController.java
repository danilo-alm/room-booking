package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.ApiPaths;
import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.dto.AmenityResponseDTO;
import com.danilo.roombooking.service.amenity.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.Amenity.ROOT)
@RequiredArgsConstructor
public class AmenityController {
    private final AmenityService amenityService;

    @PostMapping(ApiPaths.Amenity.CREATE)
    public ResponseEntity<AmenityResponseDTO> createAmenity(@RequestBody AmenityRequestDTO amenityRequestDTO) {
        return ResponseEntity.ok(amenityService.createAmenity(amenityRequestDTO));
    }

    @GetMapping(ApiPaths.Amenity.GET)
    public ResponseEntity<List<AmenityResponseDTO>> getAmenities(
        @RequestParam(required = false) String prefix) {
        return ResponseEntity.ok(amenityService.getAmenities(prefix));
    }

    @DeleteMapping(ApiPaths.Amenity.DELETE)
    public ResponseEntity<Void> deleteAmenity(@PathVariable BigInteger id) {
        amenityService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
    }
}
