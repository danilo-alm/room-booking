package com.danilo.roombooking.dto;

import java.sql.Timestamp;

public record BookingRequestDTO(Long roomId, Timestamp startTime, Timestamp endTime) {
}
