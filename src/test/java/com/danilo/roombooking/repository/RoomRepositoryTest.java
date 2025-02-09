package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
public class RoomRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    public void RoomRepository_SaveAndRetrieveRoom_ReturnsRoom() {
        Room room = createAndSaveRoom("A101", "Lecture Hall A101", RoomType.LECTURE_HALL, RoomStatus.AVAILABLE, 100);
        Optional<Room> foundRoom = roomRepository.findById(room.getId());

        assertTrue(foundRoom.isPresent());
        assertEquals("A101", foundRoom.get().getIdentifier());
        assertEquals(RoomType.LECTURE_HALL, foundRoom.get().getType());
    }

    @Test
    public void RoomRepository_FindByIdentifier_ReturnsRoom() {
        Room room = createAndSaveRoom("B202", "Computer Lab B202", RoomType.COMPUTER_LAB, RoomStatus.AVAILABLE, 30);
        Optional<Room> foundRoom = roomRepository.findByIdentifier("B202");

        assertTrue(foundRoom.isPresent());
        assertEquals(room.getName(), foundRoom.get().getName());
    }

    @Test
    public void RoomRepository_FindByIdentifier_ReturnsEmptyOptional() {
        Optional<Room> foundRoom = roomRepository.findByIdentifier("Z999");

        assertFalse(foundRoom.isPresent());
    }

    @Test
    public void RoomRepository_Save_UpdatesRoom() {
        Room room = createAndSaveRoom("C303", "Seminar Room C303", RoomType.SEMINAR_ROOM, RoomStatus.AVAILABLE, 50);
        room.setStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(room);

        Optional<Room> updatedRoom = roomRepository.findByIdentifier("C303");

        assertTrue(updatedRoom.isPresent());
        assertEquals(RoomStatus.MAINTENANCE, updatedRoom.get().getStatus());
    }

    @Test
    public void RoomRepository_DeleteById_DeletesRoom() {
        Room room = createAndSaveRoom("D404", "Auditorium D404", RoomType.AUDITORIUM, RoomStatus.AVAILABLE, 200);
        roomRepository.deleteById(room.getId());

        Optional<Room> deletedRoom = roomRepository.findById(room.getId());

        assertFalse(deletedRoom.isPresent());
    }

    private Room createAndSaveRoom(String identifier, String name, RoomType type, RoomStatus status, int capacity) {
        Room room = Room.builder().build();
        room.setIdentifier(identifier);
        room.setName(name);
        room.setType(type);
        room.setStatus(status);
        room.setCapacity(capacity);
        room.setDescription("Test Room");

        return roomRepository.save(room);
    }
}