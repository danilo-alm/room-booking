package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.security.CustomUserDetails;
import com.danilo.roombooking.config.web.ApiPaths;
import com.danilo.roombooking.domain.Booking;
import com.danilo.roombooking.dto.BookingFilterDTO;
import com.danilo.roombooking.dto.BookingRequestDTO;
import com.danilo.roombooking.dto.BookingResponseDTO;
import com.danilo.roombooking.service.booking.BookingService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.Timestamp;

@RestController
@RequestMapping(ApiPaths.Booking.ROOT)
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Operations related to room bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping(ApiPaths.Booking.CREATE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a booking",
        description = "Creates a new booking and returns the created resource with its generated ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Booking successfully created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content),
        @ApiResponse(responseCode = "409", description = "Room is unavailable or occupied during the requested time slot.",
            content = @Content),
    })
    public ResponseEntity<BookingResponseDTO> create(
        @Parameter(description = "Booking details", required = true)
        @RequestBody BookingRequestDTO bookingRequestDTO,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Booking booking = bookingService.create(bookingRequestDTO, customUserDetails);
        URI loc = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path(ApiPaths.Booking.GET_BY_ID)
            .buildAndExpand(booking.getId())
            .toUri();
        return ResponseEntity.created(loc).body(new BookingResponseDTO(booking));
    }

    @GetMapping(ApiPaths.Booking.GET)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get all bookings",
        description = "Retrieves a paginated list of all bookings."
    )
    @ApiResponse(responseCode = "200", description = "Successful retrieval of bookings",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingResponseDTO.class)))
    public ResponseEntity<Page<BookingResponseDTO>> getAll(
        @Parameter(description = "Pagination and sorting information",
            example = "?page=0&size=10&sort=startTime,desc")
        @PageableDefault Pageable pageable
    ) {
        Page<BookingResponseDTO> bookings = bookingService.getAll(pageable).map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping(ApiPaths.Booking.GET_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get a booking by its ID",
        description = "Retrieves a single booking by its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Booking not found",
            content = @Content),
    })
    public ResponseEntity<BookingResponseDTO> getById(
        @Parameter(description = "ID of the booking", required = true, example = "1")
        @PathVariable Long id
    ) {
        BookingResponseDTO booking = new BookingResponseDTO(bookingService.getById(id));
        return ResponseEntity.ok(booking);
    }

    @GetMapping(ApiPaths.Booking.GET_BY_ROOMID)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get bookings by room ID",
        description = "Retrieves a paginated list of bookings for a specific room."
    )
    @ApiResponse(responseCode = "200", description = "Successful retrieval of bookings",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingResponseDTO.class)))
    public ResponseEntity<Page<BookingResponseDTO>> getByRoomId(
        @Parameter(description = "ID of the room", required = true, example = "2")
        @PathVariable Long roomId,

        @Parameter(description = "Pagination and sorting information",
            example = "?page=0&size=10&sort=startTime,desc")
        @PageableDefault Pageable pageable
    ) {
        Page<BookingResponseDTO> bookings = bookingService.getByRoomId(roomId, pageable)
            .map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping(ApiPaths.Booking.GET_BY_USERID)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get bookings by user ID",
        description = "Retrieves a paginated list of bookings made by a specific user."
    )
    @ApiResponse(responseCode = "200", description = "Successful retrieval of bookings",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingResponseDTO.class)))
    public ResponseEntity<Page<BookingResponseDTO>> getByUserId(
        @Parameter(description = "ID of the user", required = true, example = "5")
        @PathVariable Long userId,

        @Parameter(description = "Pagination and sorting information",
            example = "?page=0&size=10&sort=startTime,desc")
        @PageableDefault Pageable pageable
    ) {
        Page<BookingResponseDTO> bookings = bookingService.getByUserId(userId, pageable)
            .map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping(ApiPaths.Booking.GET_FILTER)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Filter bookings",
        description = "Retrieves a paginated list of bookings filtered by user, room, start time, and end time."
    )
    @ApiResponse(responseCode = "200", description = "Successful retrieval of filtered bookings",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingResponseDTO.class)))
    public ResponseEntity<Page<BookingResponseDTO>> getFilter(
        @Parameter(description = "Pagination and sorting information",
            example = "?page=0&size=10&sort=startTime,desc")
        @PageableDefault Pageable pageable,

        @Parameter(description = "Filter by requestedBy", example = "5")
        @RequestParam(required = false) Long requestedBy,

        @Parameter(description = "Filter by approvedBy", example = "1")
        @RequestParam(required = false) Long approvedBy,

        @Parameter(description = "Filter by room ID", example = "2")
        @RequestParam(required = false) Long roomId,

        @Parameter(description = "Filter by minimum start time", example = "2025-03-07T08:00:00Z")
        @RequestParam(required = false) Timestamp minStartTime,

        @Parameter(description = "Filter by maximum end time", example = "2025-03-07T18:00:00Z")
        @RequestParam(required = false) Timestamp maxEndTime
    ) {
        BookingFilterDTO filter = new BookingFilterDTO(roomId, requestedBy, approvedBy, minStartTime, maxEndTime);
        Page<BookingResponseDTO> bookings = bookingService.getFilter(filter, pageable)
            .map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping(ApiPaths.Booking.APPROVE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Approve a booking request",
        description = "Approves an existing booking pending confirmation"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking successfully approved",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Booking not found",
            content = @Content),
    })
    public ResponseEntity<BookingResponseDTO> approve(
        @Parameter(description = "ID of the booking to approve", required = true)
        @PathVariable Long id,

        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Booking booking = bookingService.approveBooking(id, userDetails);
        return ResponseEntity.ok(new BookingResponseDTO(booking));
    }

    @PutMapping(ApiPaths.Booking.UPDATE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update a booking",
        description = "Updates an existing booking by its ID and returns the updated booking."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking successfully updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Booking not found",
            content = @Content),
    })
    public ResponseEntity<BookingResponseDTO> update(
        @Parameter(description = "ID of the booking to update", required = true, example = "1")
        @PathVariable Long id,

        @Parameter(description = "Updated booking details", required = true)
        @RequestBody BookingRequestDTO bookingRequestDTO
    ) {
        BookingResponseDTO updatedBooking = new BookingResponseDTO(bookingService.update(id, bookingRequestDTO));
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping(ApiPaths.Booking.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a booking",
        description = "Deletes a booking by its ID. This action is irreversible."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Booking successfully deleted",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID of the booking to delete", required = true, example = "1")
        @PathVariable Long id
    ) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
