package com.danilo.roombooking.dto;

import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;

import java.math.BigInteger;
import java.util.Set;

public record RoomFilterDTO(
    String name,
    Integer minCapacity,
    Integer maxCapacity,
    RoomStatus status,
    RoomType type,
    Set<BigInteger> amenityIds
) {}
