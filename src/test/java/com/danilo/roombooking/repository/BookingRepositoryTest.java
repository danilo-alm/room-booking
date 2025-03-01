package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.Booking;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Room room;
    private User user;
    private Booking booking;

    @BeforeEach
    void setUp() {
        room = Room.builder()
            .identifier("COMPSCI-50")
            .name("Computer Science Lab")
            .description("My description")
            .capacity(40)
            .status(RoomStatus.AVAILABLE)
            .type(RoomType.COMPUTER_LAB)
            .build();
        entityManager.persist(room);

        user = User.builder()
            .username("tux77")
            .password("123456")
            .fullName("Tux Da Silva")
            .email("tux@email.com")
            .build();
        entityManager.persist(user);

        booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStartTime(Timestamp.valueOf(LocalDateTime.now().plusHours(1)));
        booking.setEndTime(Timestamp.valueOf(LocalDateTime.now().plusHours(2)));
        entityManager.persist(booking);
    }

    @Test
    public void BookingRepository_FindByUserId_ReturnsBookings() {
        Page<Booking> result = bookingRepository.findByUserId(user.getId(), Pageable.unpaged());
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void BookingRepository_FindByRoomId_ReturnsBookings() {
        Page<Booking> result = bookingRepository.findByRoomId(room.getId(), Pageable.unpaged());
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void BookingRepository_IsRoomBookedDuringTimeRange_ReturnsTrue_WhenOverlapping() {
        boolean isBooked = bookingRepository.isRoomBookedDuringTimeRange(
            room.getId(),
            Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)),
            Timestamp.valueOf(LocalDateTime.now().plusHours(1).plusMinutes(30))
        );
        assertTrue(isBooked);
    }

    @Test
    public void BookingRepository_IsRoomBookedDuringTimeRange_ReturnsFalse_WhenNoOverlap() {
        boolean isBooked = bookingRepository.isRoomBookedDuringTimeRange(
            room.getId(),
            Timestamp.valueOf(LocalDateTime.now().plusHours(3)),
            Timestamp.valueOf(LocalDateTime.now().plusHours(4))
        );
        assertFalse(isBooked);
    }

    @Test
    public void BookingRepository_IsRoomBookedDuringTimeRangeExcludingCurrentBooking_ReturnsTrue_WhenOverlapping() {
        boolean isBooked = bookingRepository.isRoomBookedDuringTimeRangeExcludingCurrentBooking(
            room.getId(),
            999L,
            Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)),
            Timestamp.valueOf(LocalDateTime.now().plusHours(1).plusMinutes(30))
        );
        assertTrue(isBooked);
    }

    @Test
    public void BookingRepository_IsRoomBookedDuringTimeRangeExcludingCurrentBooking_ReturnsFalse_WhenCheckingSameBooking() {
        boolean isBooked = bookingRepository.isRoomBookedDuringTimeRangeExcludingCurrentBooking(
            room.getId(),
            booking.getId(),
            Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
            Timestamp.valueOf(LocalDateTime.now().plusHours(2))
        );
        assertFalse(isBooked);
    }
}

