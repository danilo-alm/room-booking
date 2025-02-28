package com.danilo.roombooking.dto;

import com.danilo.roombooking.domain.Booking;

import java.sql.Timestamp;

public record BookingResponseDTO(Long id, Long roomId, Long userId, Timestamp startTime,
                                 Timestamp endTime, Timestamp createdAt, Timestamp updatedAt) {
    public BookingResponseDTO(Booking booking) {
        this(
            booking.getId(),
            booking.getRoom().getId(),
            booking.getUser().getId(),
            booking.getStartTime(),
            booking.getEndTime(),
            booking.getCreatedAt(),
            booking.getUpdatedAt()
        );
    }
}
