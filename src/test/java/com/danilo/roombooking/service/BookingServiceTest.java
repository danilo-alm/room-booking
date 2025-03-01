package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.Booking;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.dto.BookingRequestDTO;
import com.danilo.roombooking.repository.BookingRepository;
import com.danilo.roombooking.service.booking.BookingNotFoundException;
import com.danilo.roombooking.service.booking.BookingService;
import com.danilo.roombooking.service.booking.InvalidBookingException;
import com.danilo.roombooking.service.booking.UnavailableRoomException;
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
    private BookingRepository bookingRepository;

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
        booking = new Booking(1L, null, null, now, plusOneHour, null, null);
    }

    @Test
    public void BookingService_Create_CreatesBooking() {
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomService.getById(bookingRequestDTO.roomId())).thenReturn(room);
        when(userService.getById(bookingRequestDTO.userId())).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking response = bookingService.create(bookingRequestDTO);

        assertNotNull(response);
        assertEquals(response.getId(), booking.getId());

        verify(roomService).getById(bookingRequestDTO.roomId());
        verify(userService).getById(bookingRequestDTO.userId());
        verify(bookingRepository).save(any(Booking.class));
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

        UnavailableRoomException exception = assertThrows(UnavailableRoomException.class, () -> bookingService.create(bookingRequestDTO));

        verify(roomService).getById(bookingRequestDTO.roomId());
        assertEquals("Room is not available.", exception.getMessage());
    }

    @Test
    public void BookingService_GetAll_ReturnsAllBookings() {
        when(bookingRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(booking)));

        Page<Booking> response = bookingService.getAll(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(response.getContent(), List.of(booking));

        verify(bookingRepository).findAll(Pageable.unpaged());
    }

    @Test
    public void BookingService_GetById_ReturnsBooking_WhenExists() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Booking response = bookingService.getById(booking.getId());

        assertNotNull(response);
        assertEquals(booking, response);
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    public void BookingService_GetById_ThrowsException_WhenNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(1L));
    }

    @Test
    public void BookingService_GetByUserId_ReturnsBookings() {
        when(bookingRepository.findByUserId(user.getId(), Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(booking)));

        Page<Booking> response = bookingService.getByUserId(user.getId(), Pageable.unpaged());

        assertNotNull(response);
        assertEquals(List.of(booking), response.getContent());
        verify(bookingRepository).findByUserId(user.getId(), Pageable.unpaged());
    }

    @Test
    public void BookingService_GetByRoomId_ReturnsBookings() {
        when(bookingRepository.findByRoomId(room.getId(), Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(booking)));

        Page<Booking> response = bookingService.getByRoomId(room.getId(), Pageable.unpaged());

        assertNotNull(response);
        assertEquals(List.of(booking), response.getContent());
        verify(bookingRepository).findByRoomId(room.getId(), Pageable.unpaged());
    }

    @Test
    public void BookingService_Delete_DeletesBooking_WhenExists() {
        when(bookingRepository.existsById(booking.getId())).thenReturn(true);

        bookingService.delete(booking.getId());

        verify(bookingRepository).existsById(booking.getId());
        verify(bookingRepository).deleteById(booking.getId());
    }

    @Test
    public void BookingService_Delete_ThrowsException_WhenNotFound() {
        when(bookingRepository.existsById(1L)).thenReturn(false);

        assertThrows(BookingNotFoundException.class, () -> bookingService.delete(1L));

        verify(bookingRepository).existsById(booking.getId());
    }
}
