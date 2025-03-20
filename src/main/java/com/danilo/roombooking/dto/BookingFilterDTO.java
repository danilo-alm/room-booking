package com.danilo.roombooking.dto;

import java.sql.Timestamp;

public record BookingFilterDTO(
    Long roomId,
    Long requestedBy,
    Long approvedBy,
    Timestamp minStartTime,
    Timestamp maxEndTime
) {
}
