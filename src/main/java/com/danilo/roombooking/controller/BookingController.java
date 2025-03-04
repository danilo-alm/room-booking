package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.web.ApiPaths;
import com.danilo.roombooking.dto.BookingFilterDTO;
import com.danilo.roombooking.dto.BookingRequestDTO;
import com.danilo.roombooking.dto.BookingResponseDTO;
import com.danilo.roombooking.service.booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping(ApiPaths.Booking.ROOT)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping(ApiPaths.Booking.CREATE)
    public ResponseEntity<BookingResponseDTO> create(@RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingResponseDTO booking = new BookingResponseDTO(bookingService.create(bookingRequestDTO));
        return ResponseEntity.ok(booking);
    }

    @GetMapping(ApiPaths.Booking.GET)
    public ResponseEntity<Page<BookingResponseDTO>> getAll(@PageableDefault Pageable pageable) {
        Page<BookingResponseDTO> bookings = bookingService.getAll(pageable).map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping(ApiPaths.Booking.GET_BY_ID)
    public ResponseEntity<BookingResponseDTO> getById(@PathVariable Long id) {
        BookingResponseDTO booking = new BookingResponseDTO(bookingService.getById(id));
        return ResponseEntity.ok(booking);
    }

    @GetMapping(ApiPaths.Booking.GET_BY_ROOMID)
    public ResponseEntity<Page<BookingResponseDTO>> getByRoomId(@PathVariable Long roomId, @PageableDefault Pageable pageable) {
        Page<BookingResponseDTO> bookings = bookingService.getByRoomId(roomId, pageable)
            .map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping(ApiPaths.Booking.GET_BY_USERID)
    public ResponseEntity<Page<BookingResponseDTO>> getByUserId(@PathVariable Long userId, @PageableDefault Pageable pageable) {
        Page<BookingResponseDTO> bookings = bookingService.getByUserId(userId, pageable)
            .map(BookingResponseDTO::new);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping(ApiPaths.Booking.GET_FILTER)
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
    public ResponseEntity<BookingResponseDTO> update(
        @PathVariable Long id,
        @RequestBody BookingRequestDTO bookingRequestDTO
    ) {
        BookingResponseDTO updatedBooking = new BookingResponseDTO(bookingService.update(id, bookingRequestDTO));
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping(ApiPaths.Booking.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
