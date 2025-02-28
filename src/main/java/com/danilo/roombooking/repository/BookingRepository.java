package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    Page<Booking> findByUserId(Long userId, Pageable pageable);
    Page<Booking> findByRoomId(Long roomId, Pageable pageable);

    @Query("""
    SELECT COUNT(b) > 0 FROM Booking b
    WHERE b.room.id = :roomId
    AND b.startTime < :endTime
    AND b.endTime > :startTime
    """)
    boolean isRoomBookedDuringTimeRange(@Param("roomId") Long roomId,
                                        @Param("startTime") Timestamp startTime,
                                        @Param("endTime") Timestamp endTime);

    @Query("""
    SELECT COUNT(b) > 0 FROM Booking b
    WHERE b.room.id = :roomId
    AND b.startTime < :endTime
    AND b.endTime > :startTime
    AND b.id != :bookingId
    """)
    boolean isRoomBookedDuringTimeRangeExcludingCurrentBooking(@Param("roomId") Long roomId,
                                       @Param("bookingId") Long bookingId,
                                       @Param("startTime") Timestamp startTime,
                                       @Param("endTime") Timestamp endTime);
}
