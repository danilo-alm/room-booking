package com.danilo.roombooking.dto;

import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;

import java.util.Collection;

public record RoomRequestDTO(String identifier, String name, String description, Integer capacity, RoomStatus status,
                             RoomType type, Collection<Long> amenitiesIds) {
}
