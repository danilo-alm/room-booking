package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.repository.RoomAmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomAmenityService {
    private final RoomAmenityRepository roomAmenityRepository;

    public Set<Room> getRoomsWithAmenity(Amenity amenity) {
        return roomAmenityRepository.findRoomsByAmenityId(amenity.getId());
    }

    public Set<Room> getRoomsWithAmenities(Set<Amenity> amenities) {
        return roomAmenityRepository.findRoomsByAmenitiesIds(
            amenities.stream().map(Amenity::getId).collect(Collectors.toSet())
        );
    }
}
