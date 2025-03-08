package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.web.ApiPaths;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;
import com.danilo.roombooking.dto.RoomFilterDTO;
import com.danilo.roombooking.dto.RoomRequestDTO;
import com.danilo.roombooking.dto.RoomResponseDTO;
import com.danilo.roombooking.service.room.RoomService;
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
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(ApiPaths.Room.ROOT)
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Operations related to rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping(ApiPaths.Room.CREATE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a room",
        description = "Creates a new room and returns the created resource with its generated ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Room successfully created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content)
    })
    public ResponseEntity<RoomResponseDTO> create(
        @Parameter(description = "Room details", required = true)
        @RequestBody RoomRequestDTO roomRequestDTO
    ) {
        Room room = roomService.create(roomRequestDTO);
        URI loc = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path(ApiPaths.Room.GET_BY_ID)
            .buildAndExpand(room.getId())
            .toUri();
        return ResponseEntity.created(loc).body(new RoomResponseDTO(room));
    }

    @GetMapping(ApiPaths.Room.GET)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get all rooms",
        description = "Retrieves a paginated list of all rooms."
    )
    @ApiResponse(responseCode = "200", description = "Successful retrieval of rooms",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = RoomResponseDTO.class)))
    public ResponseEntity<Page<RoomResponseDTO>> getAll(
        @Parameter(description = "Pagination and sorting information", example = "?page=0&size=10&sort=name,asc")
        @PageableDefault Pageable pageable
    ) {
        Page<Room> rooms = roomService.getAll(pageable);
        return ResponseEntity.ok(rooms.map(RoomResponseDTO::new));
    }

    @GetMapping(ApiPaths.Room.GET_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get a room by its ID",
        description = "Retrieves a single room by its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Room found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Room not found",
            content = @Content)
    })
    public ResponseEntity<RoomResponseDTO> getById(
        @Parameter(description = "ID of the room", required = true, example = "1")
        @PathVariable Long id
    ) {
        Room room = roomService.getById(id);
        return ResponseEntity.ok(new RoomResponseDTO(room));
    }

    @GetMapping(ApiPaths.Room.GET_BY_IDENTIFIER)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get a room by its identifier",
        description = "Retrieves a single room by its unique identifier string."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Room found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Room not found",
            content = @Content)
    })
    public ResponseEntity<RoomResponseDTO> getByIdentifier(
        @Parameter(description = "Unique identifier of the room", required = true, example = "LAB-101")
        @PathVariable String identifier
    ) {
        Room room = roomService.getByIdentifier(identifier);
        return ResponseEntity.ok(new RoomResponseDTO(room));
    }

    @GetMapping(ApiPaths.Room.GET_FILTER)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Filter rooms",
        description = "Retrieves a paginated list of rooms filtered by various criteria."
    )
    @ApiResponse(responseCode = "200", description = "Successful retrieval of filtered rooms",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = RoomResponseDTO.class)))
    public ResponseEntity<Page<RoomResponseDTO>> getFilter(
        @Parameter(description = "Filter by room name", example = "Computer Lab")
        @RequestParam(required = false) String name,

        @Parameter(description = "Minimum room capacity", example = "10")
        @RequestParam(required = false) Integer minCapacity,

        @Parameter(description = "Maximum room capacity", example = "50")
        @RequestParam(required = false) Integer maxCapacity,

        @Parameter(description = "Room status", example = "AVAILABLE")
        @RequestParam(required = false) RoomStatus status,

        @Parameter(description = "Room type", example = "CLASSROOM")
        @RequestParam(required = false) RoomType type,

        @Parameter(description = "Filter by amenities (list of amenity IDs)", example = "[1,2,3]")
        @RequestParam(required = false) Set<Long> amenityIds,

        @Parameter(description = "Pagination and sorting information",
            example = "?page=0&size=10&sort=name,asc")
        @PageableDefault Pageable pageable
    ) {
        RoomFilterDTO roomFilterDTO = new RoomFilterDTO(name, minCapacity, maxCapacity, status, type, amenityIds);
        Page<Room> rooms = roomService.getFilter(roomFilterDTO, pageable);
        return ResponseEntity.ok(rooms.map(RoomResponseDTO::new));
    }

    @PutMapping(ApiPaths.Room.UPDATE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update a room",
        description = "Updates an existing room by its ID and returns the updated room."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Room successfully updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Room not found",
            content = @Content)
    })
    public ResponseEntity<RoomResponseDTO> update(
        @Parameter(description = "ID of the room to update", required = true, example = "1")
        @PathVariable("id") Long id,

        @Parameter(description = "Updated room details", required = true)
        @RequestBody RoomRequestDTO roomRequestDTO
    ) {
        Room updatedRoom = roomService.update(id, roomRequestDTO);
        return ResponseEntity.ok(new RoomResponseDTO(updatedRoom));
    }

    @DeleteMapping(ApiPaths.Room.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a room",
        description = "Deletes a room by its ID. This action is irreversible."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Room successfully deleted",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Room not found",
            content = @Content)
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID of the room to delete", required = true, example = "1")
        @PathVariable Long id
    ) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiPaths.Room.GET_TYPES)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get room types",
        description = "Retrieves all available room types."
    )
    @ApiResponse(responseCode = "200", description = "Successful retrieval of room types",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public ResponseEntity<Map<String, String>> getRoomTypes() {
        return ResponseEntity.ok(RoomType.getMap());
    }

    @GetMapping(ApiPaths.Room.GET_STATUS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get room statuses",
        description = "Retrieves all possible room statuses."
    )
    @ApiResponse(responseCode = "200", description = "Successful retrieval of room statuses",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public ResponseEntity<Map<String, String>> getRoomStatus() {
        return ResponseEntity.ok(RoomStatus.getMap());
    }

}
