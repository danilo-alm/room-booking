package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.Booking;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.dto.BookingRequestDTO;
import com.danilo.roombooking.repository.jpa.BookingJpaRepository;
import com.danilo.roombooking.service.booking.BookingConflictException;
import com.danilo.roombooking.service.booking.BookingNotFoundException;
import com.danilo.roombooking.service.booking.BookingService;
import com.danilo.roombooking.service.booking.InvalidBookingException;
import com.danilo.roombooking.service.room.RoomService;
import com.danilo.roombooking.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingJpaRepository bookingJpaRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingService bookingService;

    private Room room;
    private User user;

    private BookingRequestDTO bookingRequestDTO;
    private Booking booking;

    @BeforeEach
    void setUp() {
        Timestamp now = Timestamp.from(Instant.now());
        Timestamp plusOneHour = Timestamp.from(Instant.now().plus(1, ChronoUnit.HOURS));

        room = new Room();
        user = new User();

        bookingRequestDTO = new BookingRequestDTO(1L, 1L, now, plusOneHour);
        booking = new Booking(1L, room, user, now, plusOneHour, null, null);
    }

    @Test
    public void BookingService_Create_CreatesBooking() {
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomService.getById(bookingRequestDTO.roomId())).thenReturn(room);
        when(userService.getById(bookingRequestDTO.userId())).thenReturn(user);
        when(bookingJpaRepository.save(any(Booking.class))).thenReturn(booking);

        Booking response = bookingService.create(bookingRequestDTO);

        assertNotNull(response);
        assertEquals(response.getId(), booking.getId());

        verify(roomService).getById(bookingRequestDTO.roomId());
        verify(userService).getById(bookingRequestDTO.userId());
        verify(bookingJpaRepository).save(any(Booking.class));
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenStartTimeIsNull() {
        BookingRequestDTO invalidRequest = new BookingRequestDTO(bookingRequestDTO.roomId(), bookingRequestDTO.userId(), null, bookingRequestDTO.endTime());

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, () -> bookingService.create(invalidRequest));
        assertEquals("startTime is required.", exception.getMessage());
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenEndTimeIsNull() {
        BookingRequestDTO invalidRequest = new BookingRequestDTO(bookingRequestDTO.roomId(), bookingRequestDTO.userId(), bookingRequestDTO.startTime(), null);

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, () -> bookingService.create(invalidRequest));
        assertEquals("endTime is required.", exception.getMessage());
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenUserIdIsNull() {
        BookingRequestDTO invalidRequest = new BookingRequestDTO(bookingRequestDTO.roomId(), null, bookingRequestDTO.startTime(), bookingRequestDTO.endTime());

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, () -> bookingService.create(invalidRequest));
        assertEquals("userId is required.", exception.getMessage());
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenRoomIdIsNull() {
        BookingRequestDTO invalidRequest = new BookingRequestDTO(null, bookingRequestDTO.userId(), bookingRequestDTO.startTime(), bookingRequestDTO.endTime());

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, () -> bookingService.create(invalidRequest));
        assertEquals("roomId is required.", exception.getMessage());
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenStartTimeIsAfterEndTime() {
        BookingRequestDTO invalidRequest = new BookingRequestDTO(bookingRequestDTO.roomId(), bookingRequestDTO.userId(), bookingRequestDTO.endTime(), bookingRequestDTO.startTime());

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, () -> bookingService.create(invalidRequest));

        assertEquals("startTime cannot be after endTime.", exception.getMessage());
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenRoomIsNotAvailable() {
        room.setStatus(RoomStatus.MAINTENANCE);

        when(roomService.getById(bookingRequestDTO.roomId())).thenReturn(room);

        BookingConflictException exception = assertThrows(BookingConflictException.class, () -> bookingService.create(bookingRequestDTO));

        verify(roomService).getById(bookingRequestDTO.roomId());
        assertEquals("Room is not available.", exception.getMessage());
    }

    @Test
    public void BookingService_GetAll_ReturnsAllBookings() {
        when(bookingJpaRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(booking)));

        Page<Booking> response = bookingService.getAll(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(response.getContent(), List.of(booking));

        verify(bookingJpaRepository).findAll(Pageable.unpaged());
    }

    @Test
    public void BookingService_GetById_ReturnsBooking_WhenExists() {
        when(bookingJpaRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Booking response = bookingService.getById(booking.getId());

        assertNotNull(response);
        assertEquals(booking, response);
        verify(bookingJpaRepository).findById(booking.getId());
    }

    @Test
    public void BookingService_GetById_ThrowsException_WhenNotFound() {
        when(bookingJpaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(1L));
    }

    @Test
    public void BookingService_GetByUserId_ReturnsBookings() {
        when(bookingJpaRepository.findByUserId(user.getId(), Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(booking)));

        Page<Booking> response = bookingService.getByUserId(user.getId(), Pageable.unpaged());

        assertNotNull(response);
        assertEquals(List.of(booking), response.getContent());
        verify(bookingJpaRepository).findByUserId(user.getId(), Pageable.unpaged());
    }

    @Test
    public void BookingService_GetByRoomId_ReturnsBookings() {
        when(bookingJpaRepository.findByRoomId(room.getId(), Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(booking)));

        Page<Booking> response = bookingService.getByRoomId(room.getId(), Pageable.unpaged());

        assertNotNull(response);
        assertEquals(List.of(booking), response.getContent());
        verify(bookingJpaRepository).findByRoomId(room.getId(), Pageable.unpaged());
    }

    @Test
    public void BookingService_Update_UpdatesBooking_WhenValid() {
        when(bookingJpaRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingJpaRepository.isRoomBookedDuringTimeRangeExcludingCurrentBooking(any(), any(), any(), any())).thenReturn(false);

        BookingRequestDTO updateRequest = new BookingRequestDTO(booking.getRoom().getId(), booking.getUser().getId(), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis() + 3600000));

        Booking response = bookingService.update(booking.getId(), updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.startTime(), response.getStartTime());
        assertEquals(updateRequest.endTime(), response.getEndTime());
        verify(bookingJpaRepository).findById(booking.getId());
    }

    @Test
    public void BookingService_Update_ThrowsException_WhenBookingNotFound() {
        when(bookingJpaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.update(1L, bookingRequestDTO));
    }

    @Test
    public void BookingService_Update_ThrowsException_WhenRoomUnavailableInTimeInterval() {
        when(bookingJpaRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingJpaRepository.isRoomBookedDuringTimeRangeExcludingCurrentBooking(any(), any(), any(), any())).thenReturn(true);

        assertThrows(BookingConflictException.class, () -> bookingService.update(booking.getId(), bookingRequestDTO));
    }

    @Test
    public void BookingService_Delete_DeletesBooking_WhenExists() {
        when(bookingJpaRepository.existsById(booking.getId())).thenReturn(true);

        bookingService.delete(booking.getId());

        verify(bookingJpaRepository).existsById(booking.getId());
        verify(bookingJpaRepository).deleteById(booking.getId());
    }

    @Test
    public void BookingService_Delete_ThrowsException_WhenNotFound() {
        when(bookingJpaRepository.existsById(1L)).thenReturn(false);

        assertThrows(BookingNotFoundException.class, () -> bookingService.delete(1L));

        verify(bookingJpaRepository).existsById(booking.getId());
    }
}
