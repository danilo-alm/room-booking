package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.web.ApiPaths;
import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.dto.AmenityResponseDTO;
import com.danilo.roombooking.service.amenity.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(ApiPaths.Amenity.ROOT)
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @PostMapping(ApiPaths.Amenity.CREATE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AmenityResponseDTO> create(@RequestBody AmenityRequestDTO amenityRequestDTO) {
        Amenity amenity = amenityService.create(amenityRequestDTO);
        URI loc = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path(ApiPaths.Amenity.GET_BY_ID)
            .buildAndExpand(amenity.getId())
            .toUri();
        return ResponseEntity.created(loc).body(new AmenityResponseDTO(amenity));
    }

    @GetMapping(ApiPaths.Amenity.GET_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AmenityResponseDTO> getById(@PathVariable Long id) {
        Amenity amenity = amenityService.getById(id);
        return ResponseEntity.ok(new AmenityResponseDTO(amenity));
    }

    @GetMapping(ApiPaths.Amenity.GET)
    @ResponseStatus(HttpStatus.OK)
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        amenityService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
