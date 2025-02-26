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
        Amenity amenity = amenityService.createAmenity(amenityRequestDTO);
        return ResponseEntity.ok(new AmenityResponseDTO(amenity));
    }

    @GetMapping(ApiPaths.Amenity.GET)
    public ResponseEntity<List<AmenityResponseDTO>> getAmenities(@RequestParam(required = false) String prefix) {
        List<AmenityResponseDTO> amenityResponseDTOs = amenityService.getAmenities(prefix)
            .stream().map(AmenityResponseDTO::new).toList();
        return ResponseEntity.ok(amenityResponseDTOs);
    }

    @DeleteMapping(ApiPaths.Amenity.DELETE)
    public ResponseEntity<Void> deleteAmenity(@PathVariable BigInteger id) {
        amenityService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
    }
}
