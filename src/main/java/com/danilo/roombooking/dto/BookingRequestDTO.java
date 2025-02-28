package com.danilo.roombooking.dto;

import java.sql.Timestamp;

public record BookingRequestDTO(Long roomId, Long userId, Timestamp startTime, Timestamp endTime) {
}
