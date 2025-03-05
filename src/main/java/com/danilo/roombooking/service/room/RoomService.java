package com.danilo.roombooking.service.room;

import com.danilo.roombooking.config.web.ApiPaths;
import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.dto.RoomFilterDTO;
import com.danilo.roombooking.dto.RoomRequestDTO;
import com.danilo.roombooking.repository.RoomRepository;
import com.danilo.roombooking.service.amenity.AmenityService;
import com.danilo.roombooking.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final AmenityService amenityService;

    @Transactional
    public Room create(RoomRequestDTO roomRequestDTO) {
        validateRoomRequest(roomRequestDTO);

        Room room = Room.builder()
            .identifier(roomRequestDTO.identifier())
            .name(roomRequestDTO.name())
            .description(roomRequestDTO.description())
            .capacity(roomRequestDTO.capacity())
            .type(roomRequestDTO.type())
            .status(roomRequestDTO.status())
            .build();

        List<Amenity> amenities = amenityService.getByIdIn(roomRequestDTO.amenitiesIds());
        room.setAmenities(new HashSet<>(amenities));

        return roomRepository.saveAndFlush(room);
    }

    public Page<Room> getAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public Room getById(Long id) {
        return roomRepository.findById(id).orElseThrow(RoomNotFoundException::new);
    }

    public Room getByIdentifier(String identifier) {
        return roomRepository.findByIdentifier(identifier).orElseThrow(RoomNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Page<Room> getFilter(RoomFilterDTO filterDTO, Pageable pageable) {
        Specification<Room> spec = Specification
            .where(RoomSpecification.hasCapacityGreaterThanOrEqualTo(filterDTO.minCapacity()))
            .and(RoomSpecification.hasCapacityLessThanOrEqualTo(filterDTO.maxCapacity()))
            .and(RoomSpecification.nameContains(filterDTO.name()))
            .and(RoomSpecification.hasStatus(filterDTO.status()))
            .and(RoomSpecification.hasType(filterDTO.type()))
            .and(RoomSpecification.hasAmenities(filterDTO.amenityIds()));

        return roomRepository.findAll(spec, pageable);
    }

    @Transactional
    public Room update(Long roomId, RoomRequestDTO roomRequestDTO) {
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);

        if (roomRequestDTO.identifier() != null && !roomRequestDTO.identifier().isBlank())
            room.setIdentifier(roomRequestDTO.identifier());

        if (roomRequestDTO.name() != null && !roomRequestDTO.name().isBlank())
            room.setName(roomRequestDTO.name());

        if (roomRequestDTO.description() != null && !roomRequestDTO.description().isBlank())
            room.setDescription(roomRequestDTO.description());

        if (roomRequestDTO.capacity() != null && roomRequestDTO.capacity() > 0)
            room.setCapacity(roomRequestDTO.capacity());

        if (roomRequestDTO.status() != null)
            room.setStatus(roomRequestDTO.status());

        if (roomRequestDTO.type() != null)
            room.setType(roomRequestDTO.type());

        if (roomRequestDTO.amenitiesIds() != null) {
            List<Amenity> amenities = amenityService.getByIdIn(roomRequestDTO.amenitiesIds());
            room.setAmenities(new HashSet<>(amenities));
        }

        return room;
    }

    @Transactional
    public void delete(Long id) {
        if (!roomRepository.existsById(id))
            throw new RoomNotFoundException();

        roomRepository.deleteById(id);
    }

    private void validateRoomRequest(RoomRequestDTO roomRequestDTO) {
        if (roomRequestDTO.identifier() == null || roomRequestDTO.identifier().isBlank())
            throw new InvalidRoomException("identifier is required.");

        if (roomRequestDTO.name() == null || roomRequestDTO.name().isBlank())
            throw new InvalidRoomException("name is required.");

        if (roomRequestDTO.capacity() == null || roomRequestDTO.capacity() < 1)
            throw new InvalidRoomException("capacity is required and should be greater than zero.");

        if (roomRequestDTO.status() == null) {
            String getStatusesEndpoint = ApiPaths.Room.ROOT + ApiPaths.Room.GET_STATUS;
            throw new InvalidRoomException("status is required. Send a GET request to '"
                + getStatusesEndpoint + "' to see available statuses.");
        }

        if (roomRequestDTO.type() == null) {
            String getTypesEndpoint = ApiPaths.Room.ROOT + ApiPaths.Room.GET_TYPES;
            throw new InvalidRoomException("type is required. Send a GET request to '"
                + getTypesEndpoint + "' to see available types.");
        }
    }

}