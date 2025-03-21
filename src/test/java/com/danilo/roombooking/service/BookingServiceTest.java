package com.danilo.roombooking.service;

import com.danilo.roombooking.config.security.CustomUserDetails;
import com.danilo.roombooking.domain.Booking;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.privilege.PrivilegeType;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.dto.BookingRequestDTO;
import com.danilo.roombooking.repository.BookingRepository;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
    private CustomUserDetails userDetails;
    private CustomUserDetails userDetailsNoAuthority;

    @BeforeEach
    void setUp() {
        Timestamp now = Timestamp.from(Instant.now());
        Timestamp plusOneHour = Timestamp.from(Instant.now().plus(1, ChronoUnit.HOURS));

        room = Room.builder()
            .id(1L)
            .status(RoomStatus.AVAILABLE)
            .build();
        user = User.builder()
            .id(1L)
            .username("user")
            .password("password")
            .build();

        bookingRequestDTO = new BookingRequestDTO(1L, now, plusOneHour);
        booking = new Booking(1L, room, true, user, user, now, plusOneHour, null, null);

        userDetails = new CustomUserDetails(
            10L, "user", "password", true, false,
            List.of(new SimpleGrantedAuthority(PrivilegeType.APPROVE_BOOKING_REQUEST.name()))
        );

        userDetailsNoAuthority = new CustomUserDetails(
            10L, "user", "password", true, false, List.of()
        );
    }

    @Test
    void BookingService_ShouldCreateBooking_WhenUserHasApprovalPrivilege() {
        when(roomService.getById(any())).thenReturn(room);
        when(userService.getById(any())).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking actualBooking = bookingService.create(bookingRequestDTO, userDetails);

        assertNotNull(actualBooking);
        assertEquals(actualBooking.getRequestedBy(), user);
        assertEquals(actualBooking.getApprovedBy(), user);
        assertTrue(actualBooking.getApproved());

        verify(roomService).getById(any());
        verify(userService).getById(any());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void BookingService_ShouldCreateBookingRequest_WhenUserDoesNotHaveApprovalPrivilege() {
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomService.getById(any())).thenReturn(room);
        when(userService.getById(any())).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking actualBooking = bookingService.create(bookingRequestDTO, userDetailsNoAuthority);

        assertNotNull(actualBooking);
        assertEquals(actualBooking.getRequestedBy(), user);
        assertNull(actualBooking.getApprovedBy());
        assertFalse(actualBooking.getApproved());

        verify(roomService).getById(any());
        verify(userService).getById(any());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenStartTimeIsNull() {
        BookingRequestDTO invalidRequest = new BookingRequestDTO(bookingRequestDTO.roomId(), null, bookingRequestDTO.endTime());

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, () ->
            bookingService.create(invalidRequest, userDetails));
        assertEquals("startTime is required.", exception.getMessage());
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenEndTimeIsNull() {
        BookingRequestDTO invalidRequest = new BookingRequestDTO(bookingRequestDTO.roomId(), bookingRequestDTO.startTime(), null);

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, () ->
            bookingService.create(invalidRequest, userDetails));
        assertEquals("endTime is required.", exception.getMessage());
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenRoomIdIsNull() {
        BookingRequestDTO invalidRequest = new BookingRequestDTO(null, bookingRequestDTO.startTime(), bookingRequestDTO.endTime());

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, () ->
            bookingService.create(invalidRequest, userDetails));
        assertEquals("roomId is required.", exception.getMessage());
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenStartTimeIsAfterEndTime() {
        BookingRequestDTO invalidRequest = new BookingRequestDTO(bookingRequestDTO.roomId(), bookingRequestDTO.endTime(), bookingRequestDTO.startTime());

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, () ->
            bookingService.create(invalidRequest, userDetails));

        assertEquals("startTime cannot be after endTime.", exception.getMessage());
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenStartTimeIsInPast() {
        Timestamp aMinuteAgo = Timestamp.from(Instant.now().minus(1, ChronoUnit.MINUTES));
        BookingRequestDTO invalidRequest = new BookingRequestDTO(bookingRequestDTO.roomId(), aMinuteAgo, bookingRequestDTO.endTime());

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, () ->
            bookingService.create(invalidRequest, userDetails));

        assertEquals("startTime cannot be in the past.", exception.getMessage());
    }

    @Test
    public void BookingService_Create_ThrowsException_WhenRoomIsNotAvailable() {
        room.setStatus(RoomStatus.MAINTENANCE);

        when(roomService.getById(bookingRequestDTO.roomId())).thenReturn(room);

        BookingConflictException exception = assertThrows(BookingConflictException.class, () ->
            bookingService.create(bookingRequestDTO, userDetails));

        verify(roomService).getById(bookingRequestDTO.roomId());
        assertEquals("Room is unavailable or occupied during the requested time slot.", exception.getMessage());
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
    public void BookingService_GetByRequestedBy_ReturnsBookings() {
        when(bookingRepository.findByRequestedById(user.getId(), Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(booking)));

        Page<Booking> response = bookingService.getByUserId(user.getId(), Pageable.unpaged());

        assertNotNull(response);
        assertEquals(List.of(booking), response.getContent());
        verify(bookingRepository).findByRequestedById(user.getId(), Pageable.unpaged());
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
    public void BookingService_Update_UpdatesBooking_WhenValid() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.isRoomBookedDuringTimeRangeExcludingCurrentBooking(any(), any(), any(), any())).thenReturn(false);

        BookingRequestDTO updateRequest = new BookingRequestDTO(booking.getRoom().getId(), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis() + 3600000));

        Booking response = bookingService.update(booking.getId(), updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.startTime(), response.getStartTime());
        assertEquals(updateRequest.endTime(), response.getEndTime());
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    public void BookingService_Update_ThrowsException_WhenBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.update(1L, bookingRequestDTO));
    }

    @Test
    public void BookingService_Update_ThrowsException_WhenRoomUnavailableInTimeInterval() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.isRoomBookedDuringTimeRangeExcludingCurrentBooking(any(), any(), any(), any())).thenReturn(true);

        assertThrows(BookingConflictException.class, () -> bookingService.update(booking.getId(), bookingRequestDTO));
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
