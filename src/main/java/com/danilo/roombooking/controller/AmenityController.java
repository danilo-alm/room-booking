package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.web.ApiPaths;
import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.dto.AmenityRequestDTO;
import com.danilo.roombooking.dto.AmenityResponseDTO;
import com.danilo.roombooking.service.amenity.AmenityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(ApiPaths.Amenity.ROOT)
@RequiredArgsConstructor
@Tag(name = "Amenities", description = "Operations related to amenity management")
public class AmenityController {

    private final AmenityService amenityService;

    @PostMapping(ApiPaths.Amenity.CREATE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create an amenity",
        description = "Creates a new amenity and returns the created resource with its generated ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Amenity successfully created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AmenityResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content),
    })
    public ResponseEntity<AmenityResponseDTO> create(
        @Parameter(
            description = "Amenity data to be created",
            required = true
        )
        @RequestBody AmenityRequestDTO amenityRequestDTO
    ) {
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
    @Operation(
        summary = "Get an amenity by its ID",
        description = "Retrieves a single amenity by its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Amenity found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AmenityResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Amenity not found",
            content = @Content),
    })
    public ResponseEntity<AmenityResponseDTO> getById(
        @Parameter(
            description = "ID of the amenity to retrieve",
            required = true,
            example = "1"
        )
        @PathVariable Long id
    ) {
        Amenity amenity = amenityService.getById(id);
        return ResponseEntity.ok(new AmenityResponseDTO(amenity));
    }

    @GetMapping(ApiPaths.Amenity.GET)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get all amenities",
        description = "Retrieves a paginated list of amenities. If a prefix is provided, " +
            "only amenities starting with the given prefix will be returned."
    )
    @ApiResponse(responseCode = "200", description = "Successful retrieval of amenities",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AmenityResponseDTO.class)))
    public ResponseEntity<Page<AmenityResponseDTO>> getAllOrWithPrefix(
        @Parameter(
            description = "Optional prefix to filter amenities by name",
            example = "Proj"
        )
        @RequestParam(required = false) String prefix,

        @Parameter(
            description = "Pagination and sorting information",
            example = "?page=0&size=10&sort=name,asc"
        )
        @PageableDefault Pageable pageable
    ) {
        Page<Amenity> amenities = (prefix != null)
            ? amenityService.getWithPrefix(prefix, pageable)
            : amenityService.getAll(pageable);

        return ResponseEntity.ok(amenities.map(AmenityResponseDTO::new));
    }

    @DeleteMapping(ApiPaths.Amenity.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete an amenity",
        description = "Deletes an amenity by its ID. This action is irreversible."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Amenity successfully deleted",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Amenity not found",
            content = @Content)
    })
    public ResponseEntity<Void> delete(
        @Parameter(
            description = "ID of the amenity to delete",
            required = true,
            example = "1"
        )
        @PathVariable Long id
    ) {
        amenityService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
