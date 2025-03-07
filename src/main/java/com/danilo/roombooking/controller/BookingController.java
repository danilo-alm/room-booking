package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.web.ApiPaths;
import com.danilo.roombooking.domain.Booking;
import com.danilo.roombooking.dto.BookingFilterDTO;
import com.danilo.roombooking.dto.BookingRequestDTO;
import com.danilo.roombooking.dto.BookingResponseDTO;
import com.danilo.roombooking.service.booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.Timestamp;

@RestController
@RequestMapping(ApiPaths.Booking.ROOT)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping(ApiPaths.Booking.CREATE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookingResponseDTO> create(@RequestBody BookingRequestDTO bookingRequestDTO) {
        Booking booking = bookingService.create(bookingRequestDTO);
        URI loc = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path(ApiPaths.Booking.GET_BY_ID)
            .buildAndExpand(booking.getId())
            .toUri();
        return ResponseEntity.created(loc).body(new BookingResponseDTO(booking));
    }

    @GetMapping(ApiPaths.Booking.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<BookingResponseDTO>> getAll(@PageableDefault Pageable pageable) {
        Page<BookingResponseDTO> bookings = bookingService.getAll(pageable).map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping(ApiPaths.Booking.GET_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BookingResponseDTO> getById(@PathVariable Long id) {
        BookingResponseDTO booking = new BookingResponseDTO(bookingService.getById(id));
        return ResponseEntity.ok(booking);
    }

    @GetMapping(ApiPaths.Booking.GET_BY_ROOMID)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<BookingResponseDTO>> getByRoomId(@PathVariable Long roomId, @PageableDefault Pageable pageable) {
        Page<BookingResponseDTO> bookings = bookingService.getByRoomId(roomId, pageable)
            .map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping(ApiPaths.Booking.GET_BY_USERID)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<BookingResponseDTO>> getByUserId(@PathVariable Long userId, @PageableDefault Pageable pageable) {
        Page<BookingResponseDTO> bookings = bookingService.getByUserId(userId, pageable)
            .map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping(ApiPaths.Booking.GET_FILTER)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<BookingResponseDTO>> getFilter(
        @PageableDefault Pageable pageable,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long roomId,
        @RequestParam(required = false) Timestamp minStartTime,
        @RequestParam(required = false) Timestamp maxEndTime
    ) {
        BookingFilterDTO filter = new BookingFilterDTO(roomId, userId, minStartTime, maxEndTime);
        Page<BookingResponseDTO> bookings = bookingService.getFilter(filter, pageable)
            .map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping(ApiPaths.Booking.UPDATE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BookingResponseDTO> update(
        @PathVariable Long id,
        @RequestBody BookingRequestDTO bookingRequestDTO
    ) {
        BookingResponseDTO updatedBooking = new BookingResponseDTO(bookingService.update(id, bookingRequestDTO));
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping(ApiPaths.Booking.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
