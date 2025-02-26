package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;
import com.danilo.roombooking.dto.RoomRequestDTO;
import com.danilo.roombooking.repository.AmenityRepository;
import com.danilo.roombooking.repository.RoomRepository;
import com.danilo.roombooking.service.room.RoomNotFoundException;
import com.danilo.roombooking.service.room.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private RoomService roomService;

    BigInteger roomId;
    Room room;
    RoomRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        roomId = BigInteger.ONE;

        requestDTO = createRoomRequestDTO("R101", "Classroom 101", 50);

        room = createRoom(BigInteger.ONE, "R101", "Classroom 101", 50);
        room.setType(RoomType.SCIENCE_LAB);
    }

    @Test
    public void RoomService_CreateRoom_ReturnsCreatedRoom() {
        when(roomRepository.saveAndFlush(any(Room.class))).thenReturn(room);

        Room response = roomService.createRoom(requestDTO);

        assertNotNull(response);
        assertEquals("R101", response.getIdentifier());
        assertEquals("Classroom 101", response.getName());
        assertEquals(50, response.getCapacity());

        verify(roomRepository).saveAndFlush(any(Room.class));
    }

    @Test
    public void RoomService_GetRoom_ById_ReturnsRoom() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Room response = roomService.getRoomById(roomId);

        assertNotNull(response);
        assertEquals("R101", response.getIdentifier());
        assertEquals("Classroom 101", response.getName());

        verify(roomRepository).findById(roomId);
    }

    @Test
    public void RoomService_GetRoom_ByIdentifier_ReturnsRoom() {
        String identifier = room.getIdentifier();
        when(roomRepository.findByIdentifier(identifier)).thenReturn(Optional.of(room));

        Room response = roomService.getRoomByIdentifier(identifier);

        assertNotNull(response);
        assertEquals("R101", response.getIdentifier());
        assertEquals("Classroom 101", response.getName());

        verify(roomRepository).findByIdentifier(identifier);
    }

    @Test
    public void RoomService_UpdateRoom_ReturnsUpdatedRoom() {
        requestDTO = createRoomRequestDTO("R101", "Updated Classroom", 60);

        Amenity amenity = new Amenity();
        amenity.setId(BigInteger.ONE);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(amenityRepository.findByIdIn(anyCollection())).thenReturn(List.of(amenity));

        Room response = roomService.updateRoom(roomId, requestDTO);

        assertNotNull(response);
        assertEquals("R101", response.getIdentifier());
        assertEquals("Updated Classroom", response.getName());
        assertEquals(60, response.getCapacity());

        verify(roomRepository).findById(roomId);
    }

    @Test
    public void RoomService_UpdateRoom_ThrowsException_WhenRoomNotFound() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.updateRoom(roomId, requestDTO));

        verify(roomRepository).findById(roomId);
    }

    @Test
    public void RoomService_DeleteRoom_DeletesRoom() {
        when(roomRepository.existsById(roomId)).thenReturn(true);

        roomService.deleteRoom(roomId);

        verify(roomRepository).deleteById(roomId);
    }

    @Test
    public void RoomService_DeleteRoom_ThrowsException_WhenRoomNotFound() {
        when(roomRepository.existsById(roomId)).thenReturn(false);

        assertThrows(RoomNotFoundException.class, () -> roomService.deleteRoom(roomId));

        verify(roomRepository, never()).deleteById(roomId);
    }

    private Room createRoom(BigInteger roomId, String identifier, String name, int capacity) {
        Room room = new Room();
        room.setId(roomId);
        room.setIdentifier(identifier);
        room.setName(name);
        room.setDescription("A large classroom");
        room.setCapacity(capacity);
        room.setType(RoomType.STANDARD_CLASSROOM);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setAmenities(new HashSet<>());
        return room;
    }

    private RoomRequestDTO createRoomRequestDTO(String identifier, String name, int capacity) {
        return new RoomRequestDTO(identifier, name, "A large classroom", capacity,
            RoomStatus.AVAILABLE, RoomType.STANDARD_CLASSROOM, List.of(BigInteger.ONE));
    }
}
