package com.danilo.roombooking.service.room;

import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room_amenity.RoomAmenity;
import com.danilo.roombooking.dto.RoomFilterDTO;
import com.danilo.roombooking.dto.RoomRequestDTO;
import com.danilo.roombooking.dto.RoomResponseDTO;
import com.danilo.roombooking.repository.AmenityRepository;
import com.danilo.roombooking.repository.RoomRepository;
import com.danilo.roombooking.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final AmenityRepository amenityRepository;

    @Transactional
    public RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO) {
        Room room = Room.builder()
            .identifier(roomRequestDTO.identifier())
            .name(roomRequestDTO.name())
            .description(roomRequestDTO.description())
            .capacity(roomRequestDTO.capacity())
            .type(roomRequestDTO.type())
            .status(roomRequestDTO.status())
            .build();

        room.setAmenities(
            getRoomAmenitySet(room, roomRequestDTO.amenitiesIds())
        );

        roomRepository.saveAndFlush(room);
        return new RoomResponseDTO(room);
    }

    @Transactional(readOnly = true)
    public Optional<RoomResponseDTO> getRoom(BigInteger id, String identifier) {
        long nonNullCount = Stream.of(id, identifier).filter(Objects::nonNull).count();
        if (nonNullCount != 1) {
            return Optional.empty();
        }
        if (id != null) {
            return Optional.of(this.getRoomById(id));
        }
        return Optional.of(this.getRoomByIdentifier(identifier));
    }

    public List<RoomResponseDTO> getFilterRooms(RoomFilterDTO filterDTO) {
        Specification<Room> spec = Specification
            .where(RoomSpecification.hasCapacityGreaterThanOrEqualTo(filterDTO.minCapacity()))
            .and(RoomSpecification.hasCapacityLessThanOrEqualTo(filterDTO.maxCapacity()))
            .and(RoomSpecification.nameContains(filterDTO.name()))
            .and(RoomSpecification.hasStatus(filterDTO.status()))
            .and(RoomSpecification.hasType(filterDTO.type()))
            .and(RoomSpecification.hasAmenities(filterDTO.amenityIds()));

        return roomRepository.findAll(spec).stream()
            .map(RoomResponseDTO::new)
            .toList();
    }

    @Transactional
    public RoomResponseDTO updateRoom(BigInteger roomId, RoomRequestDTO roomRequestDTO) {
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);

        if (roomRequestDTO.identifier() != null) {
            room.setIdentifier(roomRequestDTO.identifier());
        }
        if (roomRequestDTO.name() != null) {
            room.setName(roomRequestDTO.name());
        }
        if (roomRequestDTO.description() != null) {
            room.setDescription(roomRequestDTO.description());
        }
        if (roomRequestDTO.capacity() != null) {
            room.setCapacity(roomRequestDTO.capacity());
        }
        if (roomRequestDTO.status() != null) {
            room.setStatus(roomRequestDTO.status());
        }
        if (roomRequestDTO.type() != null) {
            room.setType(roomRequestDTO.type());
        }
        if (roomRequestDTO.amenitiesIds() != null) {
            room.setAmenities(getRoomAmenitySet(room, roomRequestDTO.amenitiesIds()));
        }

        return new RoomResponseDTO(room);
    }

    @Transactional
    public void deleteRoom(BigInteger id) {
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException();
        }
        roomRepository.deleteById(id);
    }

    private Set<RoomAmenity> getRoomAmenitySet(Room room, Collection<BigInteger> amenitiesIds) {
        return amenityRepository.findByIdIn(amenitiesIds).stream().map(amenity ->
            new RoomAmenity(room, amenity)).collect(Collectors.toSet());
    }

    private RoomResponseDTO getRoomById(BigInteger id) {
        Room room = roomRepository.findById(id).orElseThrow(RoomNotFoundException::new);
        return new RoomResponseDTO(room);
    }

    private RoomResponseDTO getRoomByIdentifier(String identifier) {
        Room room = roomRepository.findByIdentifier(identifier).orElseThrow(RoomNotFoundException::new);
        return new RoomResponseDTO(room);
    }
}