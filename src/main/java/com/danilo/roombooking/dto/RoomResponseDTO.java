package com.danilo.roombooking.dto;

import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

public record RoomResponseDTO(Long id, String identifier, String name, String description, Integer capacity, RoomStatus status,
                              RoomType type, Set<String> amenities, Timestamp createdAt, Timestamp updatedAt) {
    public RoomResponseDTO(Room room) {
        this(
            room.getId(),
            room.getIdentifier(),
            room.getName(),
            room.getDescription(),
            room.getCapacity(),
            room.getStatus(),
            room.getType(),
            room.getAmenities().stream().map(
                roomAmenity -> roomAmenity.getAmenity().getName()
            ).collect(Collectors.toSet()),
            room.getCreatedAt(),
            room.getUpdatedAt()
        );
    }
}
