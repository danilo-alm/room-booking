package com.danilo.roombooking.service.booking;

import com.danilo.roombooking.config.security.CustomUserDetails;
import com.danilo.roombooking.domain.Booking;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.privilege.PrivilegeType;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final UserService userService;
    private final RoomService roomService;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking create(BookingRequestDTO bookingRequestDTO, CustomUserDetails userDetails) {
        validateBookingRequest(bookingRequestDTO);
        checkRoomAvailabilityInTimeInterval(bookingRequestDTO);

        Room room = roomService.getById(bookingRequestDTO.roomId());
        if (room.getStatus() != RoomStatus.AVAILABLE)
            throw new BookingConflictException();

        User requestedBy = userService.getById(userDetails.getUserId());
        User approvedBy = userDetails.getAuthorities()
            .contains(new SimpleGrantedAuthority(PrivilegeType.APPROVE_BOOKING_REQUEST.name()))
            ? requestedBy : null;

        Booking booking = Booking.builder()
            .room(room)
            .requestedBy(requestedBy)
            .approvedBy(approvedBy)
            .approved(approvedBy != null)
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
        return bookingRepository.findByRequestedById(userId, pageable);
    }

    public Page<Booking> getByRoomId(Long roomId, Pageable pageable) {
        return bookingRepository.findByRoomId(roomId, pageable);
    }

    public Page<Booking> getFilter(BookingFilterDTO bookingFilterDTO, Pageable pageable) {
        Specification<Booking> spec = Specification
            .where(BookingSpecification.hasRoomId(bookingFilterDTO.roomId()))
            .and(BookingSpecification.hasRequestedByEquals(bookingFilterDTO.requestedBy()))
            .and(BookingSpecification.hasApprovedByEquals(bookingFilterDTO.approvedBy()))
            .and(BookingSpecification.hasStartTimeGreaterThanOrEqualTo(bookingFilterDTO.minStartTime()))
            .and(BookingSpecification.hasEndTimeLessThanOrEqualTo(bookingFilterDTO.maxEndTime()));

        return bookingRepository.findAll(spec, pageable);
    }

    @Transactional
    public Booking update(Long bookingId, BookingRequestDTO bookingRequestDTO) {
        Booking booking = getById(bookingId);

        Timestamp startTime = bookingRequestDTO.startTime();
        Timestamp endTime = bookingRequestDTO.endTime();

        BookingRequestDTO updateDTO = new BookingRequestDTO(
            booking.getRoom().getId(),
            startTime != null ? startTime : booking.getStartTime(),
            endTime != null ? endTime : booking.getEndTime()
        );

        checkRoomUpdateAvailabilityInTimeInterval(bookingId, updateDTO);

        booking.setStartTime(updateDTO.startTime());
        booking.setEndTime(updateDTO.endTime());

        return booking;
    }

    @Transactional
    public Booking approveBooking(Long bookingId, CustomUserDetails userDetails) {
        Booking booking = getById(bookingId);
        User approvedBy = userService.getById(userDetails.getUserId());

        booking.setApproved(true);
        booking.setApprovedBy(approvedBy);

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

        if (bookingRequestDTO.roomId() == null)
            throw new InvalidBookingException("roomId is required.");

        if (bookingRequestDTO.startTime().after(bookingRequestDTO.endTime()))
            throw new InvalidBookingException("startTime cannot be after endTime.");
    }

    private void checkRoomAvailabilityInTimeInterval(BookingRequestDTO bookingRequestDTO) {
        boolean isUnavailable = bookingRepository.isRoomBookedDuringTimeRange(
            bookingRequestDTO.roomId(), bookingRequestDTO.startTime(), bookingRequestDTO.endTime());

        if (isUnavailable) throw new BookingConflictException();
    }

    private void checkRoomUpdateAvailabilityInTimeInterval(Long bookingId, BookingRequestDTO bookingRequestDTO) {
        boolean conflicts = bookingRepository.isRoomBookedDuringTimeRangeExcludingCurrentBooking(
            bookingRequestDTO.roomId(), bookingId, bookingRequestDTO.startTime(), bookingRequestDTO.endTime());

        if (conflicts) throw new BookingConflictException();
    }

}
