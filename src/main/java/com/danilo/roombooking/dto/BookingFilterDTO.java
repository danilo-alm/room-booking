package com.danilo.roombooking.dto;

import java.sql.Timestamp;

public record BookingFilterDTO(
    Long roomId,
    Long userId,
    Timestamp minStartTime,
    Timestamp maxEndTime
) {
}
