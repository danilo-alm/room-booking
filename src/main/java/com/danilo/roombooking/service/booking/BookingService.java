package com.danilo.roombooking.service.booking;

import com.danilo.roombooking.domain.Booking;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.dto.BookingFilterDTO;
import com.danilo.roombooking.dto.BookingRequestDTO;
import com.danilo.roombooking.repository.BookingRepository;
import com.danilo.roombooking.service.room.RoomService;
import com.danilo.roombooking.service.user.UserService;
import com.danilo.roombooking.specification.BookingSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final UserService userService;
    private final RoomService roomService;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking create(BookingRequestDTO bookingRequestDTO) {
        validateBookingRequest(bookingRequestDTO);
        checkRoomAvailabilityInTimeInterval(bookingRequestDTO);

        Room room = roomService.getById(bookingRequestDTO.roomId());
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new UnavailableRoomException("Room is not available.");
        }

        User user = userService.getById(bookingRequestDTO.userId());

        Booking booking = Booking.builder()
            .room(room)
            .user(user)
            .startTime(bookingRequestDTO.startTime())
            .endTime(bookingRequestDTO.endTime())
            .build();

        return bookingRepository.save(booking);
    }

    public Page<Booking> getAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    public Booking getById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(BookingNotFoundException::new);
    }

    public Page<Booking> getByUserId(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable);
    }

    public Page<Booking> getByRoomId(Long roomId, Pageable pageable) {
        return bookingRepository.findByRoomId(roomId, pageable);
    }

    public Page<Booking> getFilter(BookingFilterDTO bookingFilterDTO, Pageable pageable) {
        Specification<Booking> spec = Specification
            .where(BookingSpecification.hasRoomId(bookingFilterDTO.roomId())
            .and(BookingSpecification.hasUserId(bookingFilterDTO.userId())
            .and(BookingSpecification.hasStartTimeGreaterThanOrEqualTo(bookingFilterDTO.minStartTime()))
            .and(BookingSpecification.hasEndTimeLessThanOrEqualTo(bookingFilterDTO.maxEndTime())))
        );

        return bookingRepository.findAll(spec, pageable);
    }

    @Transactional
    public Booking update(Long bookingId, BookingRequestDTO bookingRequestDTO) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(BookingNotFoundException::new);

        Timestamp startTime = bookingRequestDTO.startTime();
        Timestamp endTime = bookingRequestDTO.endTime();

        BookingRequestDTO updateDTO = new BookingRequestDTO(
            booking.getRoom().getId(),
            booking.getUser().getId(),
            startTime != null ? startTime : booking.getStartTime(),
            endTime != null ? endTime : booking.getEndTime()
        );

        checkRoomUpdateAvailabilityInTimeInterval(bookingId, updateDTO);

        booking.setStartTime(updateDTO.startTime());
        booking.setEndTime(updateDTO.endTime());

        return booking;
    }

    public void delete(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingNotFoundException();
        }
        bookingRepository.deleteById(bookingId);
    }

    private void validateBookingRequest(BookingRequestDTO bookingRequestDTO) {
        if (bookingRequestDTO.startTime() == null)
            throw new InvalidBookingException("startTime is required.");

        if (bookingRequestDTO.endTime() == null)
            throw new InvalidBookingException("endTime is required.");

        if (bookingRequestDTO.userId() == null)
            throw new InvalidBookingException("userId is required.");

        if (bookingRequestDTO.roomId() == null)
            throw new InvalidBookingException("roomId is required.");

        if (bookingRequestDTO.startTime().after(bookingRequestDTO.endTime()))
            throw new InvalidBookingException("startTime cannot be after endTime.");
    }

    private void checkRoomAvailabilityInTimeInterval(BookingRequestDTO bookingRequestDTO) {
        boolean isAvailable = bookingRepository.isRoomBookedDuringTimeRange(
            bookingRequestDTO.roomId(), bookingRequestDTO.startTime(), bookingRequestDTO.endTime());

        if (!isAvailable) throw new BookingConflictException();
    }

    private void checkRoomUpdateAvailabilityInTimeInterval(Long bookingId, BookingRequestDTO bookingRequestDTO) {
        boolean conflicts = bookingRepository.isRoomBookedDuringTimeRangeExcludingCurrentBooking(
            bookingRequestDTO.roomId(), bookingId, bookingRequestDTO.startTime(), bookingRequestDTO.endTime());

        if (conflicts) throw new BookingConflictException();
    }

}
