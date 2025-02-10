package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;
import com.danilo.roombooking.domain.room_amenity.RoomAmenity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RoomAmenityRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private RoomAmenityRepository roomAmenityRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Test
    public void RoomAmenityRepository_FindRoomsByAmenityId_ReturnsRooms() {
        Amenity projector = createAndSaveAmenity("Projector");
        Room room = createAndSaveRoom("A101", "Lecture Hall A101", RoomType.LECTURE_HALL, RoomStatus.AVAILABLE, 100);

        linkRoomWithAmenity(room, projector);

        Set<Room> rooms = roomAmenityRepository.findRoomsByAmenityId(projector.getId());

        assertFalse(rooms.isEmpty());
        assertEquals(1, rooms.size());
        assertEquals(room.getIdentifier(), rooms.iterator().next().getIdentifier());
    }

    @Test
    public void RoomAmenityRepository_FindRoomsByAmenitiesIds_ReturnsRooms() {
        Amenity projector = createAndSaveAmenity("Projector");
        Amenity whiteboard = createAndSaveAmenity("Whiteboard");

        Room room1 = createAndSaveRoom("B202", "Computer Lab B202", RoomType.COMPUTER_LAB, RoomStatus.AVAILABLE, 30);
        Room room2 = createAndSaveRoom("C303", "Seminar Room C303", RoomType.SEMINAR_ROOM, RoomStatus.AVAILABLE, 50);

        linkRoomWithAmenity(room1, projector);
        linkRoomWithAmenity(room2, whiteboard);

        Set<Room> rooms = roomAmenityRepository.findRoomsByAmenitiesIds(Set.of(projector.getId(), whiteboard.getId()));

        assertFalse(rooms.isEmpty());
        assertEquals(2, rooms.size());
    }

    @Test
    public void RoomAmenityRepository_FindRoomsByAmenityId_ReturnsEmptySet() {
        Amenity nonExistentAmenity = createAndSaveAmenity("NonExistent");
        Set<Room> rooms = roomAmenityRepository.findRoomsByAmenityId(nonExistentAmenity.getId());

        assertTrue(rooms.isEmpty());
    }

    @Test
    public void RoomAmenityRepository_Save_CreatesRoomAmenityRelation() {
        Amenity wifi = createAndSaveAmenity("WiFi");
        Room room = createAndSaveRoom("D404", "Auditorium D404", RoomType.AUDITORIUM, RoomStatus.AVAILABLE, 200);

        linkRoomWithAmenity(room, wifi);

        Set<Room> rooms = roomAmenityRepository.findRoomsByAmenityId(wifi.getId());

        assertFalse(rooms.isEmpty());
        assertEquals(room.getIdentifier(), rooms.iterator().next().getIdentifier());
    }

    private Room createAndSaveRoom(String identifier, String name, RoomType type, RoomStatus status, int capacity) {
        Room room = new Room();
        room.setIdentifier(identifier);
        room.setName(name);
        room.setType(type);
        room.setStatus(status);
        room.setCapacity(capacity);
        room.setDescription("Test Room");

        return roomRepository.save(room);
    }

    private Amenity createAndSaveAmenity(String name) {
        Amenity amenity = new Amenity();
        amenity.setName(name);

        return amenityRepository.save(amenity);
    }

    private void linkRoomWithAmenity(Room room, Amenity amenity) {
        RoomAmenity roomAmenity = new RoomAmenity();
        roomAmenity.setRoom(room);
        roomAmenity.setAmenity(amenity);

        roomAmenityRepository.save(roomAmenity);
    }
}
